package com.lzf.stackwatcher.agent.data;


/**
* @author 李子帆
* @version 1.0
* @date 2018年11月27日 下午9:47:43
* @Description 类说明
*/
public final class NovaNetworkIOData extends CurrentNovaData {

	private static final long serialVersionUID = 5679265265610902340L;
	
	public final DeviceData[] data;
	
	public static final class DeviceData {
		public final String device;		//网络接口名
	
		public final long rxBytes;		//接收的字节数
		public final long rxPackets;	//接收的包数
		public final long rxErrors;		//接收过程发生的异常数量
		public final long rxDrop;		//接收过程发生的丢包数量
		
		public final long rxBytesSpeed;
		public final long rxPacketSpeed;
		
		public final long txBytes;		//发送的字节数
		public final long txPackets;	//发送的包数
		public final long txErrors;		//发送过程发生的异常数
		public final long txDrop;		//发送过程发生的丢包数
		
		public final long txByteSpeed;
		public final long txPacketSpeed;
		
		public DeviceData(String device ,long rxBytes, long rxPackets,long rxErros, 
				long rxDrop, long txBytes, long txPackets, long txErros, long txDrop,
				long rxByteSpeed, long rxPacketSpeed, long txByteSpeed, long txPacketSpeed) {
			this.device = device;
			this.rxBytes = rxBytes;
			this.rxPackets = rxPackets;
			this.rxErrors = rxErros;
			this.rxDrop = rxDrop;
			this.txBytes = txBytes;
			this.txPackets = txPackets;
			this.txErrors = txErros;
			this.txDrop = txDrop;
			this.rxBytesSpeed = rxByteSpeed;
			this.rxPacketSpeed = rxPacketSpeed;
			this.txByteSpeed = txByteSpeed;
			this.txPacketSpeed = txPacketSpeed;
		}
	}

	public NovaNetworkIOData(String host, DeviceData[] data) {
		super(host, NOVA_NETWORK_IO);
		this.data = data;
	}
}
