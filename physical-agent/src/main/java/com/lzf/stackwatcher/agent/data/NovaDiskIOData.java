package com.lzf.stackwatcher.agent.data;

/**
* @author 李子帆
* @version 1.0
* @date 2018年11月27日 下午9:51:25
* @Description 类说明
*/
public final class NovaDiskIOData extends CurrentNovaData {

	private static final long serialVersionUID = 1963306356081214689L;
	
	public final DeviceData[] data;
	
	public static final class DeviceData {	
		public final String device;
		public final long read;
		public final long write;
		public final long reBytes;
		public final long wrBytes;
		
		public DeviceData(String device, long read, long write, long reBytes, long wrBytes) {
			this.device = device;
			this.read = read;
			this.write = write;
			this.reBytes = reBytes;
			this.wrBytes = wrBytes;
		}
	}
	

	public NovaDiskIOData(String host, DeviceData[] data) {
		super(host, NOVA_DISK_IO);
		this.data = data;
	}

}
