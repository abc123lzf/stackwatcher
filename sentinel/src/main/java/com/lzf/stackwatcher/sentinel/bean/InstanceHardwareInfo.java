package com.lzf.stackwatcher.sentinel.bean;

public class InstanceHardwareInfo {
    public String host;
    public int id;
    public String uuid;
    public String name;

    public int status;

    public String vmType;
    public String osType;

    public long memory;
    public long curMemory;
    public int vcpus;

    public NetworkInterfaceInfo[] interfaceInfo;
    public DiskInfo[] diskInfo;

    public static final class NetworkInterfaceInfo {
        public String macAddr;	//网络接口的MAC地址
        public String bridge;		//网桥名
        public String netDevice;  //网络设备名
        public String source; 	//网络名称，对应LibvirtXML的虚拟机网络名称信息
        public NetworkInterfaceInfo(String macAddr, String bridge,
                                    String netDevice, String src) {
            this.macAddr = macAddr;
            this.bridge = bridge;
            this.netDevice = netDevice;
            this.source = src;
        }
    }

    public static final class DiskInfo {
        public String type;			//磁盘类型
        public String diskDevice;		//磁盘设备名
        public DiskInfo(String type, String diskDevice) {
            this.type = type;
            this.diskDevice = diskDevice;
        }
    }

    public InstanceHardwareInfo() { }

    public InstanceHardwareInfo(String host, int id, String uuid, String name, int status, String vmType, String osType,
                        long memory, long curMemory, int vcpus, NetworkInterfaceInfo[] interfaceData,
                        DiskInfo[] diskData) {
        this.host = host;
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.status = status;
        this.vmType = vmType;
        this.osType = osType;
        this.memory = memory;
        this.curMemory = curMemory;
        this.vcpus = vcpus;
        this.interfaceInfo = interfaceData;
        this.diskInfo = diskData;
    }

}
