package com.lzf.stackwatcher.agent;
/**
 * 第十届中国大学生服务外包创新创业大赛
 * 团队：s1mple  选题：A02
 */

import com.lzf.stackwatcher.common.Container;

/**
 * 
 * @author 李子帆
 * @time 2018年11月20日 上午11:31:20
 */
public interface Service<T> extends Container<T> {
	
	String serviceName();
}
