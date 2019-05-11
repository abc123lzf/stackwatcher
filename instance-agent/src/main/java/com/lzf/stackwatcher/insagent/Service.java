package com.lzf.stackwatcher.insagent;
/**
* @author 李子帆
* @version 1.0
* @date 2018年12月12日 下午9:31:21
* @Description 类说明
*/
public interface Service<T> extends Container<T> {
	
	String serviceName();
	
}
