package com.lzf.stackwatcher.agent.data;

import org.libvirt.DomainInterfaceStats;

/**
* @author 李子帆
* @version 1.0
* @date 2018年11月23日 上午10:49:08
* @Description 类说明
*/
public final class InstanceNetworkIOData extends CurrentInstanceData {
	
	private static final long serialVersionUID = 985252282167054540L;
	
	public final DeviceData[] data;
	
	public static final class DeviceData {
		public final String device;		//网络接口名
		public final String uuid;		//网络接口UUID
		
		public final int rxByteSpeed;
		public final int rxPacketSpeed;
	
		public final long rxBytes;		//接收的字节数
		public final long rxPackets;	//接收的包数
		public final long rxErrors;		//接收过程发生的异常数量
		public final long rxDrop;		//接收过程发生的丢包数量
		
		public final int txByteSpeed;
		public final int txPacketSpeed;
		
		public final long txBytes;		//发送的字节数
		public final long txPackets;	//发送的包数
		public final long txErrors;		//发送过程发生的异常数
		public final long txDrop;		//发送过程发生的丢包数
		
		public DeviceData(String device, String uuid, long rxBytes, long rxPackets, long rxErros, long rxDrop, 
				long txBytes, long txPackets, long txErros, long txDrop, int rxByteSpeed, 
				int rxPacketSpeed, int txByteSpeed, int txPacketSpeed) {
			this.device = device;
			this.uuid = uuid;
			this.rxBytes = rxBytes;
			this.rxPackets = rxPackets;
			this.rxErrors = rxErros;
			this.rxDrop = rxDrop;
			this.txBytes = txBytes;
			this.txPackets = txPackets;
			this.txErrors = txErros;
			this.txDrop = txDrop;
			this.rxByteSpeed = rxByteSpeed;
			this.rxPacketSpeed = rxPacketSpeed;
			this.txByteSpeed = txByteSpeed;
			this.txPacketSpeed = txPacketSpeed;
		}
	}
	
	public InstanceNetworkIOData(String host, int id, String uuid, String name, String device, DeviceData[] data) {
		super(host, id, uuid, name, INSTANCE_NETWORK_IO);
		this.data = data;
	}
	
	public InstanceNetworkIOData(String host, int id, String uuid, String name, String[] devices, String[] uuids, 
			DomainInterfaceStats[] newStats, DomainInterfaceStats[] oldStats, long dt) {
		super(host, id, uuid, name, INSTANCE_NETWORK_IO);
		int len;
		this.data = new DeviceData[len = newStats.length];
		for(int i = 0; i < len; i++) {
			DomainInterfaceStats dis = newStats[i];
			DomainInterfaceStats odis = oldStats[i];
			data[i] = new DeviceData(devices[i], uuids[i], dis.rx_bytes , dis.rx_packets, dis.rx_errs,
					dis.rx_drop, dis.tx_bytes, dis.tx_packets, dis.tx_errs, dis.tx_drop,
					(int)((double)(dis.rx_bytes - odis.rx_bytes) / dt * 1000), 
					(int)((double)(dis.rx_packets - odis.rx_packets) / dt * 1000),
					(int)((double)(dis.tx_bytes - odis.tx_bytes) / dt * 1000), 
					(int)((double)(dis.tx_packets - odis.tx_packets) / dt * 1000));
			
		}
	}
}
