package com.lzf.stackwatcher.agent.data;
/**
* @author 李子帆
* @version 1.0
* @date 2018年11月23日 下午12:38:25
* 虚拟机运行状态基本数据
*/
public final class InstanceData extends CurrentInstanceData {
	
	private static final long serialVersionUID = 8869175379582398342L;

	public final int status;
	
	public final String vmType;
	public final String osType;
	
	public final long memory;
	public final long curMemory;
	public final int vcpus;
	
	public final NetworkInterfaceData[] interfaceData;
	public final DiskData[] diskData;
	
	public static final class NetworkInterfaceData {
		public final String type;		//网络接口类型
		public final String macAddr;	//网络接口的MAC地址
		public final String bridge;		//网桥名
		public final String netDevice;  //网络设备名
		public final String source; 	//网络名称，对应LibvirtXML的虚拟机网络名称信息
		public NetworkInterfaceData(String type, String macAddr, String bridge, 
									String netDevice, String src) {
			this.macAddr = macAddr;
			this.bridge = bridge;
			this.netDevice = netDevice;
			this.type = type;
			this.source = src;
		}
	}
	
	public static final class DiskData {
		public final String type;			//磁盘类型
		public final String diskDevice;		//磁盘设备名
		public DiskData(String type, String diskDevice) {
			this.type = type;
			this.diskDevice = diskDevice;
		}
	}

	public InstanceData(String host, int id, String uuid, String name, int status, String vmType, String osType,
			long memory, long curMemory, int vcpus, NetworkInterfaceData[] interfaceData, 
			DiskData[] diskData) {
		super(host, id, uuid, name, INSTANCE_INFO);
		this.status = status;
		this.vmType = vmType;
		this.osType = osType;
		this.memory = memory;
		this.curMemory = curMemory;
		this.vcpus = vcpus;
		this.interfaceData = interfaceData;
		this.diskData = diskData;
	}

}
