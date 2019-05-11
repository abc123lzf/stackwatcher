package com.lzf.stackwatcher.insagent;
/**
* @author 李子帆
* @version 1.0
* @date 2018年12月12日 下午9:32:29
* @Description 类说明
*/

import com.lzf.stackwatcher.insagent.data.CPUData;
import com.lzf.stackwatcher.insagent.data.DiskData;
import com.lzf.stackwatcher.insagent.data.MemoryData;
import com.lzf.stackwatcher.insagent.data.NetworkData;

public interface MonitorService extends Service<Agent> {
	
	String SERVICE_NAME = "service.mointor";
	
	default String serviceName() {
		return SERVICE_NAME;
	}

	CPUData currentCPUData();
	
	MemoryData currentMemoryMointorData();
	
	NetworkData currentNetworkData();
	
	DiskData currentDiskData();
}
