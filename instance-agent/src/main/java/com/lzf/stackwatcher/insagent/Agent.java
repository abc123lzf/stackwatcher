package com.lzf.stackwatcher.insagent;
/**
* @author 李子帆
* @version 1.0
* @date 2018年12月12日 下午9:05:54
* @Description 装载于虚拟机(云实例)中的Agent模块
*/

public interface Agent extends Container<Void> {

	String getInstanceUUID();
	
	void addService(Service<?> service);
	
	Service<?> getService(String name);
	
	<T extends Service<?>> T getService(String name, Class<T> klass); 
}
