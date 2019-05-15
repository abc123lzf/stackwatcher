package com.lzf.stackwatcher.sentinel.bean;

public class HostHardwareInfo {

    public String hostName;	//计算机主机名
    public String osName;		//计算机操作系统名称
    public String osVersion;	//计算机操作系统版本
    public String osArch;		//计算机操作系统架构
    public String localIp;	//计算机本地IP

    public long memory;		//内存容量
    public long swapMemory;	//虚拟内存容量

    public String cpuVendor;	//CPU制造商
    public String cpuName;		//CPU型号
    public int cpuMhz;		//CPU频率
    public int cpuNum;		//CPU数量

    public NovaDiskInfo[] diskInfo;		//磁盘信息
    public NovaNetworkInfo[] networkInfo;	//网卡信息

    public static final class NovaDiskInfo {
        public String name;	//磁盘名
        public String path;	//磁盘挂载路径
        public String type;	//磁盘文件系统类型
        public long size;		//磁盘大小，单位KB

        public NovaDiskInfo(String name, String path, String type, long size) {
            this.name = name;
            this.path = path;
            this.type = type;
            this.size = size;
        }
    }

    public static final class NovaNetworkInfo {
        public String name;		//网卡名称
        public String type;		//网卡类型
        public String ipAddr;		//IP地址
        public String netmask;	//子网掩码
        public String broadCast;	//网关广播地址
        public String macAddr;	//网卡MAC地址
        public String desc;		//网卡描述信息
        public NovaNetworkInfo(String name, String type, String ipAddr, String netmask,
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

    public HostHardwareInfo() { }

    public HostHardwareInfo(String hostName, String osName, String osVersion, String osArch,
                            String localIp, long memory, long swapMemory, String cpuVendor, String cpuName,
                            int cpuMhz, int cpuNum, NovaDiskInfo[] diskData, NovaNetworkInfo[] networkData) {
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
        this.diskInfo = diskData;
        this.networkInfo = networkData;
    }
}
