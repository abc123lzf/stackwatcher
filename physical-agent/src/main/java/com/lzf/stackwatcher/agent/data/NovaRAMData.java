package com.lzf.stackwatcher.agent.data;
/**
* @author 李子帆
* @version 1.0
* @date 2018年11月27日 下午9:35:44
* @Description 类说明
*/
public final class NovaRAMData extends CurrentNovaData {

	private static final long serialVersionUID = 405401114407237734L;
	
	public final long size;
	public final long used;
	
	public final long swapSize;
	public final long swapUsed;

	public NovaRAMData(String host, long size, long used, long swapSize, long swapUsed) {
		super(host, NOVA_RAM);
		this.size = size;
		this.used = used;
		this.swapSize = swapSize;
		this.swapUsed = swapUsed;
	}

}
