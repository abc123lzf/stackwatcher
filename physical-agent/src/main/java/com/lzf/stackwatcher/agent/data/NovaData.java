package com.lzf.stackwatcher.agent.data;
/**
* @author 李子帆
* @version 1.0
* @date 2018年11月27日 下午7:31:00
* @Description 类说明
*/
public final class NovaData implements Data {

	private static final long serialVersionUID = -8802060287755020491L;
	
	public final String type = NOVA_INFO;
	public final String uuid = NOVA_UUID;
	
	public final String name;		//计算机名
	public final String hostName;	//计算机主机名
	public final String osName;		//计算机操作系统名称
	public final String osVersion;	//计算机操作系统版本
	public final String osArch;		//计算机操作系统架构
	public final String localIp;	//计算机本地IP
	
	public final long memory;		//内存容量
	public final long swapMemory;	//虚拟内存容量
	
	public final String cpuVendor;	//CPU制造商
	public final String cpuName;		//CPU型号
	public final int cpuMhz;		//CPU频率
	public final int cpuNum;		//CPU数量
	
	public final NovaDiskData[] diskData;		//磁盘信息
	public final NovaNetworkData[] networkData;	//网卡信息
	
	public static final class NovaDiskData {
		public final String name;	//磁盘名
		public final String path;	//磁盘挂载路径
		public final String type;	//磁盘文件系统类型
		public final long size;		//磁盘大小，单位KB
		
		public NovaDiskData(String name, String path, String type, long size) {
			this.name = name;
			this.path = path;
			this.type = type;
			this.size = size;
		}
	}
	
	public static final class NovaNetworkData {
		public final String name;		//网卡名称
		public final String type;		//网卡类型
		public final String ipAddr;		//IP地址
		public final String netmask;	//子网掩码
		public final String broadCast;	//网关广播地址
		public final String macAddr;	//网卡MAC地址
		public final String desc;		//网卡描述信息
		public NovaNetworkData(String name, String type, String ipAddr, String netmask,
				String broadCast, String macAddr, String desc) {
			this.name = name;
			this.type = type;
			this.ipAddr = ipAddr;
			this.netmask = netmask;
			this.broadCast = broadCast;
			this.macAddr = macAddr;
			this.desc = desc;
		}
	}

	public NovaData(String name, String hostName, String osName, String osVersion, String osArch,
			String localIp, long memory, long swapMemory, String cpuVendor, String cpuName, 
			int cpuMhz, int cpuNum, NovaDiskData[] diskData, NovaNetworkData[] networkData) {
		this.name = name;
		this.hostName = hostName;
		this.osName = osName;
		this.osVersion = osVersion;
		this.osArch = osArch;
		this.localIp = localIp;
		this.memory = memory;
		this.swapMemory = swapMemory;
		this.cpuVendor = cpuVendor;
		this.cpuName = cpuName;
		this.cpuMhz = cpuMhz;
		this.cpuNum = cpuNum;
		this.diskData = diskData;
		this.networkData = networkData;
	}
}
