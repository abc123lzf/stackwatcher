package com.lzf.stackwatcher.insagent.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lzf.stackwatcher.insagent.MonitorService;
import com.lzf.stackwatcher.insagent.TransferService;
import org.apache.log4j.Logger;

import com.lzf.stackwatcher.insagent.Agent;
import com.lzf.stackwatcher.insagent.ContainerBase;
import com.lzf.stackwatcher.insagent.Service;

/**
* @author 李子帆
* @version 1.0
* @date 2018年12月12日 下午9:12:07
* @Description 类说明
*/
public class StandardAgent extends ContainerBase<Void> implements Agent {
	private static final Logger log = Logger.getLogger(StandardAgent.class);
	
	private Map<String, Service<?>> serviceMap;
	
	private String uuid;
	
	public StandardAgent() {
		serviceMap = new ConcurrentHashMap<>();
		this.uuid = System.getenv("INSTANCE_UUID");
		if(uuid == null) {
			log.error("请设置环境变量INSTANCE_UUID，其值为当前虚拟机的UUID");
			System.exit(1);
		}
		MonitorService ms = new StandardMonitorService(this);
		TransferService ts = new StandardTransferService(this);
		
		serviceMap.put(ms.serviceName(), ms);
		serviceMap.put(ts.serviceName(), ts);
		
		setName("Agent");
	}
	
	@Override
	protected void initInternal() {
		for(Map.Entry<String, Service<?>> entry : serviceMap.entrySet()) {
			Service<?> service = entry.getValue();
			service.init();
		}
	}
	
	@Override
	protected void startInternal() {
		for(Map.Entry<String, Service<?>> entry : serviceMap.entrySet()) {
			Service<?> service = entry.getValue();
			service.start();
		}
	}
	
	@Override
	protected void stopInternal() {
		for(Map.Entry<String, Service<?>> entry : serviceMap.entrySet()) {
			Service<?> service = entry.getValue();
			service.stop();
		}
	}

	@Override
	public void addService(Service<?> service) {
		serviceMap.put(service.serviceName(), service);
	}

	@Override
	public String getInstanceUUID() {
		return uuid;
	}

	@Override
	public Service<?> getService(String name) {
		return serviceMap.get(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Service<?>> T getService(String name, Class<T> klass) {
		return (T)serviceMap.get(name);
	}
	
}
