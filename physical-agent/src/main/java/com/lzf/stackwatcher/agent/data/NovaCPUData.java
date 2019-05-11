package com.lzf.stackwatcher.agent.data;
/**
* @author 李子帆
* @version 1.0
* @date 2018年11月27日 下午9:28:30
* @Description 类说明
*/
public class NovaCPUData extends CurrentNovaData {

	private static final long serialVersionUID = -9068487499292744794L;
	
	public final float total;
	public final float system;
	public final float iowait;
	public final float user;
	public final float other;

	public NovaCPUData(String host, float total, float system, 
				float iowait, float user, float other) {
		super(host, NOVA_CPU);
		this.total = total;
		this.system = system;
		this.iowait = iowait;
		this.user = user;
		this.other = other;
	}

}
