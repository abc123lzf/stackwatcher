package com.lzf.stackwatcher.agent.data;

/**
* @author 李子帆
* @version 1.0
* @date 2018年11月27日 下午9:55:47
* @Description 类说明
*/
public final class NovaDiskCapacityData extends CurrentNovaData {

	private static final long serialVersionUID = 4760374245671286955L;

	public final DeviceData[] data;
	
	public static final class DeviceData {	
		public final String device;
		
		public final long capacity;
		public final long used;
		public final float usedutilization;
		
		public DeviceData(String device, long cap, long used) {
			this.device = device;
			this.capacity = cap;
			this.used = used;
			this.usedutilization = (float)((double)used / cap);
		}
	}
	
	public NovaDiskCapacityData(String host, DeviceData[] data) {
		super(host, NOVA_DISK_CAP);
		this.data = data;
	}

}
