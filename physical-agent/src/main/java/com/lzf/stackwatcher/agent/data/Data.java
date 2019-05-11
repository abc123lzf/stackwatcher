package com.lzf.stackwatcher.agent.data;

import com.alibaba.fastjson.JSON;

/**
* @author 李子帆
* @version 1.0
* @date 2018年11月23日 下午1:02:15
* @Description 监控数据接口
*/
public interface Data extends java.io.Serializable {

	String NOVA_UUID = System.getenv("NOVA_UUID");
	
	String NOVA_INFO = "nova.info";
	String NOVA_CPU = "nova.cpu";
	String NOVA_RAM = "nova.ram";
	String NOVA_NETWORK_IO = "nova.netio";
	String NOVA_DISK_IO = "nova.diskio";
	String NOVA_DISK_CAP = "nova.diskcap";
	
	String INSTANCE_INFO = "ins.info";
	String INSTANCE_CPU = "ins.cpu";
	String INSTANCE_RAM = "ins.ram";
	String INSTANCE_NETWORK_IO = "ins.netio";
	String INSTANCE_DISK_IO = "ins.diskio";
	String INSTANCE_DISK_CAP = "ins.diskcap";
	
	default String toJSON() {
		return JSON.toJSONString(this);
	}

	static String toJSON(Object obj) {
		return JSON.toJSONString(obj);
	}
}
