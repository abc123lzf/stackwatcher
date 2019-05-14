package com.lzf.stackwatcher.agent;

import java.util.List;
/**
* @author 李子帆
* @version 1.0
* @date 2018年12月9日 下午1:20:16
* @Description 虚拟机的基本管理，包含虚拟机的创造、销毁、启动、关闭等
*/
public interface DomainManagerService extends Service<Agent> {
	
	String DEFAULT_SERVICE_NAME = "service.domainManager";
	String DEFAULT_CONFIG_NAME = "config.domainManager";

	
	MonitorService getMonitorService();
	
	@Override
	default String serviceName() {
		return DEFAULT_SERVICE_NAME;
	}

	/**
	 * 获取所有实例的UUID
	 * @return 包含所有虚拟机实例的UUID集合
	 */
	List<String> getAllInstanceUUID();
	
	/**
	 * @return 宿主机名
	 */
	String getHostName();

}
