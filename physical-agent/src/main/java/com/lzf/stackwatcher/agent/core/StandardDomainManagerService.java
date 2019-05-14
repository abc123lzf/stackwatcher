package com.lzf.stackwatcher.agent.core;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.libvirt.*;
import org.libvirt.DomainInfo.DomainState;

import com.lzf.stackwatcher.common.ContainerBase;

import com.lzf.stackwatcher.agent.Agent;
import com.lzf.stackwatcher.agent.DomainManagerService;
import com.lzf.stackwatcher.agent.MonitorService;

/**
* @author 李子帆
* @version 1.0
* @date 2018年12月9日 下午1:21:44
* @Description 监控数据源
*/
public class StandardDomainManagerService extends ContainerBase<Agent> implements DomainManagerService {
	private static final Logger log = Logger.getLogger(StandardDomainManagerService.class);

	private final Agent agent;


	private final MonitorService monitorService;
	
	//Libvirt连接
	protected Connect libvirtConn;
	
	//用于执行定时任务，比如监控虚拟机
	protected ScheduledExecutorService executor;
	
	//存放当前主机的虚拟机操作对象
	protected List<LibvirtDomainAdaptor> instanceAdaptors;
	
	//存放处于开启状态的虚拟机的操作对象
	protected List<LibvirtDomainAdaptor> runningInstanceAdaptors;
	
	//计算节点操作接口
	protected NovaAdaptor novaAdaptor;
	
	public StandardDomainManagerService(Agent agent) {
		setName("DomainManagerService");
		addLifecycleEventListener(LifecycleLoggerListener.INSTANCE);
		this.agent = agent;
		this.monitorService = new StandardMonitorService(this);
	}
	
	/**
	 * 虚拟机、物理机服务组件初始化细节，该方法主要分为以下几个步骤
	 * 1、设置Libvirt API库的目录
	 * 2、向Libvirt服务端发起连接
	 * 3、创建一个线程池
	 */
	@Override
	protected void initInternal() {
		GlobalConfig gc = agent.getConfig(GlobalConfig.NAME, GlobalConfig.class);
		
		try {
			libvirtConn = new Connect(gc.libvirtAddress());
		} catch (LibvirtException e) {
			log.fatal("Libvirt连接异常，请检查Libvirt配置", e);
			System.exit(1);
		}
		
		try {
			novaAdaptor = new NovaAdaptor(this, libvirtConn);		
		} catch (LibvirtException e) {
			log.error("宿主机信息获取失败", e);
			System.exit(1);
		}
		
		executor = new ScheduledThreadPoolExecutor(11, new DiscardOldestPolicyWithLogger(getClass()));
		monitorService.init();
	}
	
	/**
	 * Libvirt服务组件启动细节，该方法分为以下几个步骤
	 * 1、获取Hypervisor所有的虚拟机对象
	 * 2、将这些虚拟机的信息封装为一个接口对象保存在集合中
	 * 3、开启监控线程，负责监控虚拟机实例
	 */
	@Override
	protected void startInternal() {
		int[] ids;
		try {
			ids = libvirtConn.listDomains();
		} catch (LibvirtException e) {
			log.error("通过Libvirt获取虚拟机信息失败", e);
			restart();
			return;
		}
		
		List<LibvirtDomainAdaptor> runList = new ArrayList<>(16);
		List<LibvirtDomainAdaptor> list = new ArrayList<>(16);
		
		for(int id : ids) {
			try {
				LibvirtDomainAdaptor c = new LibvirtDomainAdaptor(this, libvirtConn, id);
				DomainInfo info = c.currentDomainState();
				if(info != null && info.state == DomainState.VIR_DOMAIN_RUNNING)
					runList.add(c);
				list.add(c);
			} catch(LibvirtException e) {			
				log.error(format("[虚拟机ID:%s] 获取虚拟机Domain对象失败", id), e);
			} catch (DocumentException e) {
				log.error(format("[虚拟机ID:%s] 虚拟机Domain XML配置文件解析异常", id), e);
			}
		}
		
		runningInstanceAdaptors = new CopyOnWriteArrayList<>(runList);
		instanceAdaptors = new CopyOnWriteArrayList<>(list);
		monitorService.start();
	}
	
	@Override
	protected void stopInternal() {
		try {
			libvirtConn.close();
		} catch (LibvirtException e) {
			log.warn("Libvirt连接关闭失败", e);
		}
		executor.shutdown();
		monitorService.stop();
	}
	
	@Override
	protected void restartInternal() {
		executor.shutdownNow();
		try {
			libvirtConn.close();
		} catch (LibvirtException e) {
			log.warn("Libvirt连接关闭失败", e);
		}
		init();
		start();
	}


	@Override
	public Agent getParent() {
		return agent;
	}
	
	@Override
	public MonitorService getMonitorService() {
		return monitorService;
	}
	
	@Override
	public List<String> getAllInstanceUUID() {
		List<String> list = new ArrayList<>(runningInstanceAdaptors.size());
		for(LibvirtDomainAdaptor c : runningInstanceAdaptors)
			list.add(c.uuid);
		return list;
	}
	
	@Override
	public String getHostName() {
		return novaAdaptor.hostName;
	}
	
	private LibvirtDomainAdaptor findLibvirtDomainAdaptor(String uuid) {
		for(LibvirtDomainAdaptor adaptor : instanceAdaptors) {
			if(adaptor.uuid.equals(uuid)) {
				return adaptor;
			}
		}
		return null;
	}
	
	/**
	 * 立刻检查虚拟机状态
	 * @param deepCheck 是否检查已存在的虚拟机中硬件配置是否发生改变(比如新增加了网卡或者磁盘)
	 */
	void checkInstanceStates(boolean deepCheck) {
		int i = 0;
		//检查之前处于开启状态但是现在处于关闭状态的虚拟机
		for(LibvirtDomainAdaptor adaptor : runningInstanceAdaptors) {
			DomainInfo info = adaptor.currentDomainState();
			if(info == null || info.state != DomainInfo.DomainState.VIR_DOMAIN_RUNNING) {
				runningInstanceAdaptors.remove(i);
			}
			i++;
		}
		//移除已经删除的虚拟机实例
		Map<String, LibvirtDomainAdaptor> insmap = new HashMap<>(32);	
		i = 0;
		for(LibvirtDomainAdaptor adaptor : instanceAdaptors) {
			insmap.put(adaptor.uuid, adaptor);
			if(adaptor.currentDomainState() == null) {
				instanceAdaptors.remove(i);
			}
			i++;
		}
		
		try {
			int[] ids = libvirtConn.listDomains();
			for(int id : ids) {
				Domain domain = libvirtConn.domainLookupByID(id);
				String uuid = domain.getUUIDString();
				
				if(!insmap.containsKey(uuid)) {
					LibvirtDomainAdaptor adaptor = new LibvirtDomainAdaptor(this, libvirtConn, id);
					instanceAdaptors.add(adaptor);
					DomainInfo info = adaptor.currentDomainState();
					if(info == null)
						return;
					if(info.state == DomainInfo.DomainState.VIR_DOMAIN_RUNNING) {
						runningInstanceAdaptors.add(adaptor);
					}
				} else if(deepCheck) {
					LibvirtDomainAdaptor adaptor = insmap.get(uuid);
					LibvirtDomainAdaptor newAda = new LibvirtDomainAdaptor(this, libvirtConn, id);
					if(!adaptor.deepEquals(newAda)) {
						instanceAdaptors.remove(adaptor);
						instanceAdaptors.add(newAda);
					}
				}
			}
		} catch (LibvirtException e) {
			log.error("获取虚拟机列表失败", e);
			restart();
		} catch (DocumentException e) {
			log.error("Domain XML文件解析异常", e);
		} catch (Exception e) {
			log.error("检查虚拟机状态时发生异常", e);
		}
	}
}
