package com.lzf.stackwatcher.insagent;

import com.lzf.stackwatcher.insagent.data.Data;

/**
* @author 李子帆
* @version 1.0
* @date 2018年12月24日 上午9:46:14
* @Description 类说明
*/
public interface TransferService extends Service<Agent> {
	String SERVICE_NAME = "service.transfer";
	
	@Override
	default String serviceName() {
		return SERVICE_NAME;
	}
	
	void transferMointorData(Data data);
}
