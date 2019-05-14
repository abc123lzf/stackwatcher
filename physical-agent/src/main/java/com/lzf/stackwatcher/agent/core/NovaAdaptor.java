/**
 * 第十届中国大学生服务外包创新创业大赛
 * 团队：s1mple  选题：A02
 */
package com.lzf.stackwatcher.agent.core;

import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.lzf.stackwatcher.agent.data.*;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;
import org.libvirt.*;

/**
 * 计算节点(物理机)基本信息、监控数据获取
 * @author 李子帆
 * @time 2018年11月22日 上午9:13:33
 */
final class NovaAdaptor {
	
	private static final Logger log = Logger.getLogger(NovaAdaptor.class);
	
	final StandardDomainManagerService service;
	final String hostName;
	
	final Map<String, String> networkUUIDs;
	final Map<String, Network> networks;
	
	final NetworkFilter[] networkFilters;
	final StoragePool[] storagePools;
	
	final NovaData novaInfo;
	
	NovaAdaptor(StandardDomainManagerService service, Connect connect) throws LibvirtException {
		this.service = service;
		this.hostName = connect.getHostName();
		
		String[] a = connect.listNetworks();
		networks = new HashMap<>();
		networkUUIDs = new HashMap<>();
		int len = a.length;
		for(int i = 0; i < len; i++) {
			String name = a[i];
			Network n = connect.networkLookupByName(name);
			try {
				String uuid = getNetworkUUID(n.getXMLDesc(0));
				networkUUIDs.put(name, uuid);
			} catch (DocumentException e) {
				log.warn("解析虚拟网络XML描述文件发生异常", e);
				System.exit(1);
			}
			networks.put(name, n);
		}
		
		a = connect.listNetworkFilters();
		networkFilters = new NetworkFilter[len = a.length];
		for(int i = 0; i < len; i++)
			networkFilters[i] = connect.networkFilterLookupByName(a[i]);
		
		a = connect.listStoragePools();
		storagePools = new StoragePool[len = a.length];
		for(int i = 0; i < len; i++)
			storagePools[i] = connect.storagePoolLookupByName(a[i]);

		this.novaInfo = novaInfomation();
		
		initNetworkMap();
	}
	
	private static final String getNetworkUUID(String xmlDesc) throws DocumentException {
		SAXReader reader = new SAXReader();
		StringReader sr = new StringReader(xmlDesc);
		Document doc = reader.read(sr);
		return doc.getRootElement().elementText("uuid");
	}
	
	/**
	 * 获取当前计算节点的硬件信息
	 * @return 包含该计算节点的所有硬件信息
	 */
	private NovaData novaInfomation() {
		Sigar sigar = new Sigar();
		Map<String, String> map = System.getenv();
		Properties props = System.getProperties();
		
		String compName = map.get("COMPUTERNAME");
		String osName = props.getProperty("os.name");
		String osArch = props.getProperty("os.arch");
		String osVersion = props.getProperty("os.version");
		String ipAddr = null;
		try {
			ipAddr = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.warn("无法获取当前计算节点的本地IP地址", e);
		}
		
		long memory = -1, swapMemory = -1;
		try {
			Mem mem = sigar.getMem();
			memory = mem.getTotal();
		} catch (SigarException e) {
			log.warn("无法获取当前计算节点的内存大小", e);
		}
		
		try {
			Swap swap = sigar.getSwap();
			swapMemory = swap.getTotal();
		} catch (SigarException e) {
			log.warn("无法获取当前计算节点的虚拟内存大小", e);
		}
		
		String cpuVendor = null, cpuName = null;
		int cpuMhz = -1, cpuNum = -1;
		try {
			CpuInfo[] infos = sigar.getCpuInfoList();
			cpuNum = infos.length;
			cpuMhz = infos[0].getMhz();
			cpuVendor = infos[0].getVendor();
			cpuName = infos[0].getModel();
		} catch (SigarException e) {
			log.warn("无法获取当前计算节点的CPU信息", e);
		}
		
		NovaData.NovaDiskData[] diskData = null;
		try {
			FileSystem[] fslist = sigar.getFileSystemList();
			int i = 0;
			diskData = new NovaData.NovaDiskData[fslist.length];
			for(FileSystem fs : fslist) {
				FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());
				diskData[i++] = new NovaData.NovaDiskData(fs.getDevName(), fs.getDirName(),
						fs.getSysTypeName(), usage.getTotal());
			}
		} catch (SigarException e) {
			log.warn("无法获取当前计算节点的磁盘信息", e);
		}
		
		NovaData.NovaNetworkData[] networkData = null;
		try {
			String[] ifNames = sigar.getNetInterfaceList();
			networkData = new NovaData.NovaNetworkData[ifNames.length];
			int i = 0;
			for(String ifName : ifNames) {
				NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(ifName);
				networkData[i++] = new NovaData.NovaNetworkData(ifName, ifconfig.getType() ,ifconfig.getAddress(),
						ifconfig.getNetmask(), ifconfig.getBroadcast(), ifconfig.getHwaddr(), 
						ifconfig.getDescription());
			}
		} catch (SigarException e) {
			log.warn("无法获取当前计算节点的网络接口信息", e);
		}

		return new NovaData(compName, hostName, osName, osVersion, osArch, ipAddr, memory, swapMemory, 
				cpuVendor, cpuName, cpuMhz, cpuNum, diskData, networkData);
	}
	
	/**
	 * 立刻获取当前计算节点的CPU使用信息
	 * @return CPU使用信息数据对象
	 */
	final NovaCPUData currentCPUUsage() {
		Sigar sigar = new Sigar();
		try {
			CpuPerc[] cpuPercs = sigar.getCpuPercList();
			float total = 0, sys = 0, user = 0, iowait = 0, other = 0;
			for(CpuPerc cpuPerc : cpuPercs) {
				total += (float)cpuPerc.getCombined();
				sys += (float)cpuPerc.getSys();
				user += (float)cpuPerc.getUser();
				iowait += (float)cpuPerc.getWait();
				other += (float)(cpuPerc.getNice() + cpuPerc.getSoftIrq() + cpuPerc.getStolen());
			}
			int len = cpuPercs.length;		
			return new NovaCPUData(hostName, total / len, sys / len, iowait / len, user / len, other / len);
		} catch (SigarException e) {
			log.error("无法获取当前计算节点的CPU使用信息", e);
			return null;
		}
	}
	
	/**
	 * 立刻获取当前计算节点的内存、虚拟内存使用信息
	 * @return 数据对象
	 */
	final NovaRAMData currentRAMUsage() {
		Sigar sigar = new Sigar();
		try {
			Mem mem = sigar.getMem();
			Swap swap = sigar.getSwap();
			return new NovaRAMData(hostName, mem.getTotal(), mem.getUsed(), swap.getTotal(), swap.getUsed());
		} catch (SigarException e) {
			log.error("无法获取当前计算节点的内存信息", e);
			return null;
		}
	}
	
	private final Map<String, NetInterfaceStat> networkMap = new ConcurrentHashMap<>();
	private volatile long networkTime;
	
	/**
	 * 立刻获取当前计算节点各网卡的IO
	 * @return 数据对象
	 */
	final NovaNetworkIOData currentNetworkIOInfo() {
		Sigar sigar = new Sigar();
		try {
			String[] ifNames = sigar.getNetInterfaceList();
			NovaNetworkIOData.DeviceData[] deviceData = new NovaNetworkIOData.DeviceData[ifNames.length];
			long bt = System.currentTimeMillis();
			for(int i = 0; i < ifNames.length; i++) {
				String name = ifNames[i];
				NetInterfaceStat s = sigar.getNetInterfaceStat(name);
				NetInterfaceStat os = null;
				if((os = networkMap.get(name)) == null) {
					networkMap.put(name, s);
					continue;
				}
				long dt = System.currentTimeMillis() - networkTime;
				deviceData[i] = new NovaNetworkIOData.DeviceData(name, s.getRxBytes(), s.getRxPackets(), s.getRxErrors(),
						s.getRxDropped(), s.getTxBytes(), s.getTxPackets(), s.getTxErrors(), s.getTxErrors(),
						(s.getRxBytes() - os.getRxBytes()) / dt,
						(s.getRxPackets() - os.getRxPackets()) / dt,
						(s.getTxBytes() - os.getTxBytes()) / dt, 
						(s.getTxPackets() - os.getTxPackets()) / dt);
				
				networkMap.put(name, s);
			}
			
			networkTime = (System.currentTimeMillis() + bt) / 2;
			
			return new NovaNetworkIOData(hostName, deviceData);
		} catch (SigarException e) {
			log.error("无法获取当前计算节点的网络IO信息", e);
			return null;
		}
	}
	
	/**
	 * 初始化网卡IO信息，便于计算速度
	 */
	private void initNetworkMap() {
		Sigar sigar = new Sigar();
		try {
			String[] ifNames = sigar.getNetInterfaceList();
			networkTime = System.currentTimeMillis();
			for(int i = 0; i < ifNames.length; i++) {
				String name = ifNames[i];
				NetInterfaceStat s = sigar.getNetInterfaceStat(name);
				networkMap.put(name, s);
			}
		} catch (SigarException e) {
			log.error("无法获取当前计算节点的网络IO信息", e);
		}
	}
	
	/**
	 * 立刻获取当前计算节点的所有磁盘(包含本地磁盘、网络磁盘等)IO
	 * @return 数据对象
	 */
	final NovaDiskIOData currentDiskIOInfo() {
		Sigar sigar = new Sigar();
		try {
			FileSystem[] fslist = sigar.getFileSystemList();
			List<NovaDiskIOData.DeviceData> ls = new ArrayList<>();
			for (int i = 0; i < fslist.length; i++) {
				FileSystem fs = fslist[i];
				//Linux文件系统中磁盘设备名以'/'开头
				if(fs.getType() == FileSystem.TYPE_LOCAL_DISK) {
					FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());
					ls.add(new NovaDiskIOData.DeviceData(fs.getDevName(), usage.getDiskReads(), 
							usage.getDiskWrites(), usage.getDiskReadBytes(), usage.getDiskWriteBytes()));
				}
			}
			return new NovaDiskIOData(hostName, ls.toArray(new NovaDiskIOData.DeviceData[ls.size()]));
		} catch (SigarException e) {
			log.error("无法获取当前计算节点的磁盘IO", e);
			return null;
		}
	}
	
	/**
	 * 立刻获取当前计算节点的所有本地磁盘使用信息
	 * @return 数据对象
	 */
	final NovaDiskCapacityData currentDiskUsage() {
		Sigar sigar = new Sigar();
		try {
			FileSystem[] fslist = sigar.getFileSystemList();
			List<NovaDiskCapacityData.DeviceData> ls = new ArrayList<>();
			
			for(FileSystem fs : fslist) {
				if(fs.getType() == FileSystem.TYPE_LOCAL_DISK && fs.getDevName().startsWith("/")) {
					FileSystemUsage fsu = sigar.getFileSystemUsage(fs.getDirName());
					ls.add(new NovaDiskCapacityData.DeviceData(fs.getDevName(), 
							fsu.getTotal(), fsu.getUsed()));
				}
			}
			
			return new NovaDiskCapacityData(hostName, ls.toArray(new NovaDiskCapacityData.DeviceData[ls.size()]));
		} catch (SigarException e) {
			return null;
		}
	}

	/**
	 * 立刻获取当前Libvirt所管理的Hypervisor中的存储池监控数据
	 * @return 数据对象
	 */
	final StoragePoolData[] currentStoragePoolData() {
		try {
			Connect c = service.libvirtConn;
			String[] pools = c.listStoragePools();
			StoragePoolData[] arr = new StoragePoolData[pools.length];
			int i = 0;
			for (String name : pools) {
				StoragePool pool = c.storagePoolLookupByName(name);
				StoragePoolInfo info = pool.getInfo();

				StoragePoolData dt = new StoragePoolData(hostName, name, pool.getUUIDString(), info.allocation,
						info.available, info.capacity, info.state.name());
				arr[i++] = dt;
			}

			return arr;
		} catch (Exception e) {
			log.warn("无法获取存储池监控数据", e);
			return null;
		}
	}

	final StorageVolData[] currentStorageVolData() {
		try {
			Connect c = service.libvirtConn;
			String[] pools = c.listStoragePools();

			int i = 0;
			List<StorageVolData> list = new ArrayList<>();
			for (String name : pools) {
				StoragePool pool = c.storagePoolLookupByName(name);
				String[] vols = pool.listVolumes();
				for(String vname : vols) {
					StorageVol vol = pool.storageVolLookupByName(vname);
					StorageVolInfo volInfo = vol.getInfo();

					StorageVolData dt = new StorageVolData(hostName, vol.getName(), pool.getName(), pool.getUUIDString(),
							volInfo.allocation, volInfo.capacity, volInfo.type == null ? "Unknown" : volInfo.type.name());

					list.add(dt);
				}
			}

			return list.toArray(new StorageVolData[list.size()]);
		} catch (Exception e) {
			log.warn("无法获取存储卷监控数据", e);
			return null;
		}
	}
}
