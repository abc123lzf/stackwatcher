/**
   * 第十届中国大学生服务外包创新创业大赛
   *团队：s1mple  选题：A02
 */
package com.lzf.stackwatcher.agent;

import com.lzf.stackwatcher.common.ConfigManager;
import com.lzf.stackwatcher.common.Container;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;


/**
 * Agent系统根容器
 * @author 李子帆
 * @time 2018年11月20日 上午11:19:14
 */
public interface Agent extends Container<Void>, ConfigManager {
	
	/**
	 * 获取Agent持有的服务对象
	 * @param serviceName 服务对象名称
	 * @return 服务对象
	 */
	Service<?> getService(String serviceName);
	
	/**
	 * 获取Agent持有的服务对象，无需强制转换
	 * @param serviceName 服务对象名称
	 * @param serviceClass 需要强制转换的类型
	 * @return 服务对象
	 */
	<T extends Service<?>> T getService(String serviceName, Class<T> serviceClass);
	
	/**
	 * 添加服务对象
	 * @param service 服务对象
	 */
	void addService(Service<?> service);

	/**
	 * @return ZooKeeper连接对象
	 */
	ZooKeeper getZooKeeper();
}
