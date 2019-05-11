package com.lzf.stackwatcher.agent.core;

import java.util.Arrays;
import java.util.List;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.DomainBlockInfo;
import org.libvirt.DomainBlockStats;
import org.libvirt.DomainInfo;
import org.libvirt.DomainInterfaceStats;
import org.libvirt.LibvirtException;
import org.libvirt.MemoryStatistic;
import org.libvirt.VcpuInfo;


/**
* @author 李子帆
* @version 1.0
* @date 2018年11月21日 下午10:04:28
* @Description 宿主机中的单个虚拟机配置信息及其操作接口
*/
final class LibvirtDomainAdaptor {
	
	private static final Logger log = Logger.getLogger(LibvirtDomainAdaptor.class);
	
	final StandardDomainManagerService service;
	final Connect connect;
	final Domain domain;
	
	final int id;				//虚拟机ID，一般从0开始计数，对应XML根结点的id
	final String uuid; 			//虚拟机UUID
	final String name; 			//虚拟机名称
	final String vmType;		//虚拟机类型，可能为kvm、qemu、xen等
	final String osType;
	
	final long memory;			//分配的内存容量
	final long curMemory;		//实际分配的内存容量，一般等于memory
	final int vcpus;			//虚拟CPU核心数
	
	//网络接口
	final NetworkInterface[] networkInterfaces;
	//磁盘信息
	final DiskInfo[] diskinfos;
	
	volatile DomainBlockStats[] blockStatsCache;
	volatile long blockStatsCacheTime;
	
	volatile DomainInterfaceStats[] interfaceCache;
	volatile long interfaceCacheTime;
	
	private int hashCodeCache;
	
	static final class NetworkInterface {
		final String type;		//网络接口类型，对应XML为interface/type
		final String macAddr;	//网络接口的MAC地址，对应XML为interface/mac/address
		final String bridge;	//网桥名，对应XML为interface/source/bridge
		final String netDevice; //网络设备名，对应XML为interface/target/dev
		final String source;	//虚拟网络名
		final String uuid;		//虚拟网络UUID
		
		private NetworkInterface(String type, String macAddr, String bridge, String netDevice, 
								 String source, String uuid) {
			this.macAddr = macAddr;
			this.bridge = bridge;
			this.netDevice = netDevice;
			this.type = type;
			this.source = source;
			this.uuid = uuid;
		}

		@Override
		public String toString() {
			return "NetworkInterface [type=" + type + ", macAddr=" + macAddr + ", bridge=" + 
					bridge + ", netDevice=" + netDevice + "]";
		}
		
		boolean deepEquals(NetworkInterface networkInterface) {
			return netDevice.equals(networkInterface.netDevice) && 
					netDevice.equals(networkInterface.macAddr);
		}
	}
	
	static final class DiskInfo {
		final String type;			//磁盘类型，对应XML为devices/disk/type，有file,block,dir,network,volume
		final String diskDevice;	//磁盘设备名，对应XML为devices/disk/target/dev
		private DiskInfo(String type, String diskDevice) {
			this.type = type;
			this.diskDevice = diskDevice;
		}
		
		@Override
		public String toString() {
			return "DiskInfo [type=" + type + ", diskDevice=" + diskDevice + "]";
		}
		
		boolean deepEquals(DiskInfo diskInfo) {
			return type.equals(diskInfo.type) && diskDevice.equals(diskInfo.diskDevice);
		}
	}
	
	LibvirtDomainAdaptor(StandardDomainManagerService service, Connect connect, int id) 
				throws LibvirtException, DocumentException {
		this.service = service;
		this.connect = connect;
		this.id = id;
		this.domain = connect.domainLookupByID(id);
		this.uuid = domain.getUUIDString();
		this.osType = domain.getOSType();
		
		Element root = decodeDomainXMLRoot(domain);
		this.name = root.elementText("name");
		this.vmType = root.attributeValue("type");
		this.memory = Integer.valueOf(root.elementText("memory"));
		this.curMemory = Integer.valueOf(root.elementText("currentMemory"));
		this.vcpus = Integer.valueOf(root.elementText("vcpu"));
		
		Element devRoot = root.element("devices");
		this.networkInterfaces = decodeDomainXMLNetworkInterface(devRoot);
		this.diskinfos = decodeDomainXMLDisk(devRoot);
		
		currentNetworkIO(); //初始化网络IO缓存
		currentDiskIO();	//初始化磁盘IO缓存
	}
	
	/**
	 * 解析虚拟机Domain XML配置文件
	 * @param domain 虚拟机对象
	 * @return 根节点对象
	 */
	private static Element decodeDomainXMLRoot(final Domain domain) 
				throws LibvirtException, DocumentException {
		String xml = domain.getXMLDesc(0);
		SAXReader sax = new SAXReader();
		StringReader reader = new StringReader(xml);
		try {	
			Document doc = sax.read(reader);
			return doc.getRootElement();
		} finally {
			reader.close();
		}
	}
	
	/**
	 * 解析虚拟机Domain XML配置文件的device标签下的网络接口信息
	 * @param devices device标签对象
	 * @return 该虚拟机所有网络接口信息
	 */
	private NetworkInterface[] decodeDomainXMLNetworkInterface(final Element devices) {
		@SuppressWarnings("unchecked")
		List<Element> list = (List<Element>)devices.elements("interface");
		NetworkInterface[] arr = new NetworkInterface[list.size()];
		int i = 0;
		for(Element ele : list) {
			String type = ele.attributeValue("bridge");
			String macAddr = ele.element("mac").attributeValue("address");
			String bridge = ele.element("source").attributeValue("bridge");
			String dev = ele.element("target").attributeValue("dev");
			String src = ele.element("source").attributeValue("network");
			String uuid = this.service.novaAdaptor.networkUUIDs.get(src);
			NetworkInterface ni = new NetworkInterface(type, macAddr, bridge, dev, src, uuid);
			arr[i++] = ni;
		}
		return arr;
	}
	
	/**
	 * 解析虚拟机Domain XML配置文件的device标签下的虚拟磁盘信息
	 * @param devices device标签对象
	 * @return 该虚拟机的磁盘信息
	 */
	private static DiskInfo[] decodeDomainXMLDisk(final Element devices) {
		@SuppressWarnings("unchecked")
		List<Element> list = (List<Element>)devices.elements("disk");
		DiskInfo[] arr = new DiskInfo[list.size()];
		int i = 0;
		for(Element ele : list) {
			String type = ele.attributeValue("type");
			String target = ele.element("target").attributeValue("dev");
			DiskInfo info = new DiskInfo(type, target);
			arr[i++] = info;
		}
		
		return arr;
	}
	
	/**
	 * 获取网络IO信息
	 * @return 包含该虚拟机所有网络接口的IO信息
	 */
	final DomainInterfaceStats[] currentNetworkIO() {
		int len;
		DomainInterfaceStats[] arr = new DomainInterfaceStats[len = networkInterfaces.length];
		int i = 0, loss = 0;
		for(NetworkInterface ni : networkInterfaces) {
			try {
				arr[i] = domain.interfaceStats(ni.netDevice);
				i++;
			} catch (LibvirtException e) {
				log.warn(String.format("[虚拟机UUID:%s] 获取虚拟网络设备:%s IO信息失败", uuid, ni.netDevice), e);
				loss++;
			}
		}
		
		if(loss > 0) {
			DomainInterfaceStats[] fix = new DomainInterfaceStats[len - loss];
			System.arraycopy(arr, 0, fix, 0, len - loss);
			interfaceCache = arr;
			interfaceCacheTime = System.currentTimeMillis();
			service.checkInstanceStates(true);
			return fix;
		}
		
		interfaceCache = arr;
		interfaceCacheTime = System.currentTimeMillis();
		return arr;
	}
	
	/**
	 * 获取磁盘占用信息
	 * @return Libvirt磁盘占用
	 */
	final DomainBlockInfo[] currentDiskInfo() {
		DomainBlockInfo[] arr = new DomainBlockInfo[diskinfos.length];
		int i = 0;
		for(DiskInfo di : diskinfos) {
			try {
				arr[i] = domain.blockInfo(di.diskDevice);
				i++;
			} catch (LibvirtException e) {
				log.warn(String.format("[虚拟机UUID:%s] 获取磁盘设备:%s 容量信息失败", uuid, di.diskDevice), e);
				service.checkInstanceStates(true);
				return currentDiskInfo();
			}
		}
		
		return arr;
	}
	
		
	final DomainBlockStats[] currentDiskIO() {
		DomainBlockStats[] arr = new DomainBlockStats[diskinfos.length];
		int i = 0;
		for(DiskInfo di : diskinfos) {
			try {
				arr[i] = domain.blockStats(di.diskDevice);
				i++;
			} catch (LibvirtException e) {
				log.warn(String.format("[虚拟机UUID:%s] 获取磁盘设备:%s IO信息失败", uuid, di.diskDevice), e);
				service.checkInstanceStates(true);
				return currentDiskIO();
			}
		}
		
		blockStatsCache = arr;
		blockStatsCacheTime = System.currentTimeMillis();
		
		return arr;
	}
	
	
	final VcpuInfo[] currentCPUStates() {
		try {
			return domain.getVcpusInfo();
		} catch (LibvirtException e) {
			log.warn(String.format("[虚拟机UUID:%s] 获取虚拟CPU信息失败", uuid), e);
			service.checkInstanceStates(false);
			return null;
		}
	}
	
	final DomainInfo currentDomainState() {
		try {
			return domain.getInfo();
		} catch (LibvirtException e) {
			log.warn(String.format("[虚拟机UUID:%s] 获取虚拟机状态失败", uuid), e);
			//不要调用checkInstanceStates方法
			return null;
		}
	}
	
	final MemoryStatistic[] currentMemoryStatistic() {
		try {
			return domain.memoryStats(10);
		} catch (LibvirtException e) {
			log.warn(String.format("[虚拟机UUID:%s] 获取虚拟机内存数据失败", uuid), e);
			service.checkInstanceStates(false);
			return null;
		}
	}
	
	@Override
	public int hashCode() {
		if(hashCodeCache == 0)
			return hashCodeCache = uuid.hashCode();
		return hashCodeCache;
	}
	
	@Override
	public boolean equals(Object object) {
		if(this == object)
			return true;
		if(object instanceof LibvirtDomainAdaptor) {
			if(this.uuid == ((LibvirtDomainAdaptor)object).uuid)
				return true;
		}
		return false;
	}
	
	boolean deepEquals(LibvirtDomainAdaptor adaptor) {
		if(uuid != adaptor.uuid)
			return false;
		if(memory != adaptor.memory || curMemory != adaptor.curMemory)
			return false;
		
		int nilen = networkInterfaces.length;
		if(nilen != adaptor.networkInterfaces.length)
			return false;
		for(int i = 0; i < nilen; i++) {
			if(!networkInterfaces[i].deepEquals(adaptor.networkInterfaces[i]))
				return false;
		}
		
		int dilen = diskinfos.length;
		if(dilen != adaptor.diskinfos.length)
			return false;
		for(int i = 0; i < dilen; i++) {
			if(!diskinfos[i].deepEquals(adaptor.diskinfos[i]))
				return false;
		}
		
		return true;
	}

	@Override
	public String toString() {
		return "LibvirtDomainAdaptor [service=" + service + ", connect=" + connect + ", domain=" + 
				domain + ", id=" + id + ", uuid=" + uuid + ", name=" + name + ", vmType=" + 
				vmType + ", osType=" + osType + ", memory=" + memory + ", curMemory=" + curMemory + 
				", vcpus=" + vcpus + ", networkInterfaces=" + Arrays.toString(networkInterfaces) + 
				", diskinfos=" + Arrays.toString(diskinfos) + "]";
	}
}
