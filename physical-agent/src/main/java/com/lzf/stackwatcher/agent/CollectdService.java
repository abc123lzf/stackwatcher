/**
 * 第十届中国大学生服务外包创新创业大赛
 * 团队：s1mple  选题：A02
 */
package com.lzf.stackwatcher.agent;

/**
 * 
 * @author 李子帆
 * @time 2019年2月16日 下午9:18:57
 */
public interface CollectdService extends Service<DomainManagerService> {
	
	String DEFAULT_SERVICE_NAME = "service.collectd";
	
	@Override
	default String serviceName() {
		return "service.collectd";
	}
	
	
}
