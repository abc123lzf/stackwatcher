package com.lzf.stackwatcher.agent.data;
/**
* @author 李子帆
* @version 1.0
* @date 2018年11月22日 下午11:09:39
* @Description 虚拟机监控数据基类，包含虚拟机的ID、UUID、名称以及数据记录时间
*/


public abstract class CurrentInstanceData extends BaseInstanceData {

	private static final long serialVersionUID = -4839280456268875310L;
	
	public final long time;		//数据记录时间
	public final String type;		//数据类型
	
	protected CurrentInstanceData(String host, int id, String uuid, String name, String dataType) {
		super(host, id, uuid, name);
		this.time = System.currentTimeMillis();
		this.type = dataType;
	}
	
}
