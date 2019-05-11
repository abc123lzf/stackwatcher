/**
 * 第十届中国大学生服务外包创新创业大赛
 * 团队：s1mple  选题：A02
 */
package com.lzf.stackwatcher.agent;

import com.lzf.stackwatcher.agent.data.BaseInstanceData;
import com.lzf.stackwatcher.agent.data.InstanceDiskCapacityData;
import com.lzf.stackwatcher.agent.data.InstanceDiskIOData;
import com.lzf.stackwatcher.agent.data.InstanceData;
import com.lzf.stackwatcher.agent.data.InstanceNetworkIOData;
import com.lzf.stackwatcher.agent.data.InstanceRAMData;
import com.lzf.stackwatcher.agent.data.InstanceVCPUData;
import com.lzf.stackwatcher.agent.data.NovaCPUData;
import com.lzf.stackwatcher.agent.data.NovaData;
import com.lzf.stackwatcher.agent.data.NovaDiskCapacityData;
import com.lzf.stackwatcher.agent.data.NovaDiskIOData;
import com.lzf.stackwatcher.agent.data.NovaNetworkIOData;
import com.lzf.stackwatcher.agent.data.NovaRAMData;

/**
 * 负责通过Libvirtd提取Hypervisor中虚拟机实例的监控数据
 * @author 李子帆
 * @time 2018年11月20日 上午11:33:44
 */
public interface MonitorService extends Service<DomainManagerService> {
	
	String DEFAULT_SERVICE_NAME = "service.monitor";

	String DEFAULT_CONFIG_NAME = "config.monitor";
	
	String JNA_PROPERTY_KEY = "jna.library.path";
	
	/**
	 * 监控服务的配置信息
	 * 配置信息来源于agent.xml
	 */
	interface Config extends com.lzf.stackwatcher.common.Config {
		boolean enable();
		//CPU监控数据采集频率，表示每隔多少秒采集一次数据，单位:秒
		int insVCPUMonitorRate();
		//内存监控数据采集频率
		int insRAMMonitorRate();
		//虚拟机网络数据采集频率
		int insNetworkIOMonitorRate();
		//磁盘IO数据采集频率
		int insDiskIOMonitorRate();
		//磁盘使用数据采集频率
		int insDiskCapacityMonitorRate();
		//物理机CPU监控数据采集频率
		int novaCPUMonitorRate();
		//物理机内存监控数据采集频率
		int novaRAMMonitorRate();
		//物理机网卡流量监控数据采集频率
		int novaNetworkIOMonitorRate();
		//物理机磁盘IO监控数据 
		int novaDiskIOMonitorRate();
		//物理机磁盘使用监控数据
		int novaDiskCapacityMonitorRate();
		//虚拟机Agent模块数据收集端口
		int insAgentRecivePort();
	}
	
	@Override
	default String serviceName() {
		return DEFAULT_SERVICE_NAME;
	}
	
	BaseInstanceData[] allInstanceBaseInfo();
	/**
	 * 获取虚拟机的状态信息
	 * @param uuid 虚拟机UUID
	 * @return 虚拟机状态监控数据
	 */
	InstanceData currentInstanceInfo(String uuid);
	
	InstanceData[] currentAllInstanceInfo();
	
	/**
	 * 立刻获取对应虚拟机的网络IO数据
	 * @param uuid 虚拟机UUID
	 * @return 网络IO数据数组，每个虚拟机的网络设备对应一个DomainInterfaceStats
	 */
	InstanceNetworkIOData currentInstanceNetworkIO(String uuid);
	
	InstanceNetworkIOData[] currentAllInstanceNetworkIO();
	
	/**
	 * 立刻获取对应虚拟机的磁盘占用信息
	 * @param uuid 虚拟机UUID
	 * @return 包含该虚拟机所有磁盘的使用信息，每个虚拟磁盘对应一个DomainBlockInfo对象
	 */
	InstanceDiskCapacityData currentInstanceDiskInfo(String uuid);
	
	InstanceDiskCapacityData[] currentAllInstanceDiskInfo();
	
	/**
	 * 立刻获取对应虚拟机的磁盘IO信息
	 * @param uuid 虚拟机UUID
	 * @return 包含该虚拟机所有磁盘的IO信息，每个虚拟磁盘对应一个DomainBlockStats对象
	 */
	InstanceDiskIOData currentInstanceDiskIO(String uuid);
	
	InstanceDiskIOData[] currentAllInstanceDiskIO();
	
	/**
	 * 立刻获取对应虚拟机的虚拟CPU使用信息
	 * @param uuid 虚拟机UUID
	 * @return 包含该虚拟机所有虚拟CPU的使用信息
	 */
	InstanceVCPUData currentInstanceVCPUUsage(String uuid);
	
	InstanceVCPUData[] currentAllInstanceVCPUUsage();
	
	/**
	 * 立刻获取对应虚拟机的内存使用信息
	 * @param uuid 虚拟机UUID
	 * @return 该虚拟机的内存数据对象
	 */
	InstanceRAMData currentInstanceRAMUsage(String uuid);
	
	InstanceRAMData[] currentAllInstanceRAMUsage();
	
	/**
	 * 获取当前计算节点(物理机)的硬件信息
	 * @return 包含该物理机的硬件信息的数据对象
	 */
	NovaData novaInfo();
	
	/**
	 * 立刻获取当前计算节点的CPU使用信息
	 * @return CPU使用信息数据对象
	 */
	NovaCPUData currentNovaCPUUsage();
	
	/**
	 * 立刻获取当前计算节点的内存、虚拟内存使用信息
	 * @return 数据对象
	 */
	NovaRAMData currentNovaRAMUsage();
	
	/**
	 * 立刻获取当前计算节点各网卡的IO
	 * @return 数据对象
	 */
	NovaNetworkIOData currentNovaNetworkIO();
	
	/**
	 * 立刻获取当前计算节点的所有磁盘(包含本地磁盘、网络磁盘等)IO
	 * @return 数据对象
	 */
	NovaDiskIOData currentNovaDiskIO();
	
	/**
	 * 立刻获取当前计算节点的所有本地磁盘使用信息
	 * @return 数据对象
	 */
	NovaDiskCapacityData currentNovaDiskInfo();
	
}
