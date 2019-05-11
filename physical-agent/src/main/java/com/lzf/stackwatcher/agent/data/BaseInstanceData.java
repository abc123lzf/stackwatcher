package com.lzf.stackwatcher.agent.data;
/**
* @author 李子帆
* @version 1.0
* @date 2018年11月26日 下午10:40:45
* @Description 基本虚拟机信息
*/
public class BaseInstanceData implements Data {

	private static final long serialVersionUID = 5141402294462002138L;
	
	public final String host;	//所在物理机主机名
	public final int id;		//虚拟机ID
	public final String uuid;	//虚拟机UUID
	public final String name;	//虚拟机名称
	
	public BaseInstanceData(String host, int id, String uuid, String name) {
		this.host = host;
		this.id = id;
		this.uuid = uuid;
		this.name = name;
	}
}
