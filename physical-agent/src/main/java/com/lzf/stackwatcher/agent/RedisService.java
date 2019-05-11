/**
 * 第十届中国大学生服务外包创新创业大赛
 * 团队：s1mple  选题：A02
 */
package com.lzf.stackwatcher.agent;

import java.util.List;

/**
 * 将Libvirt获得到的数据
 * @author 李子帆
 * @time 2018年11月20日 上午11:38:59
 */
public interface RedisService extends Service<Agent> {
	String DEFAULT_SERVICE_NAME = "service.redis";
	String DEFAULT_CONFIG_NAME = "config.redis";
	
	@Override
	default String serviceName() {
		return DEFAULT_SERVICE_NAME;
	}
	
	interface Config extends com.lzf.stackwatcher.common.Config {

		String host();
		
		int port();
		
		String pass();
		
		int databaseId();
		
		int maxTotalConnect();
		
		int maxIdleConnect();
		
		int minIdleConnect();
	}
	
	void insertList(String key, String... entries);
	
	List<String> getList(String key);
	
	void insertPair(String key, String value);

	void delete(String key);
}
