package com.lzf.stackwatcher.agent.data;
/**
* @author 李子帆
* @version 1.0
* @date 2018年11月27日 下午9:17:33
* @Description 计算节点(物理机)实时监控数据
*/
public abstract class CurrentNovaData implements Data {
	
	private static final long serialVersionUID = 5652691374381104481L;
	
	public final String host;
	public final String uuid = NOVA_UUID;
	public final String type;
	public final long time;

	protected CurrentNovaData(String host, String type) {
		this.host = host;
		this.time = System.currentTimeMillis();
		this.type = type;
	}
}
