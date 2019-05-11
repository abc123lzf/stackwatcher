package com.lzf.stackwatcher.insagent.core;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.lzf.stackwatcher.insagent.Agent;
import com.lzf.stackwatcher.insagent.ContainerBase;
import com.lzf.stackwatcher.insagent.MonitorService;
import com.lzf.stackwatcher.insagent.TransferService;
import com.lzf.stackwatcher.insagent.data.CPUData;
import com.lzf.stackwatcher.insagent.data.Data;
import com.lzf.stackwatcher.insagent.data.DiskData;
import com.lzf.stackwatcher.insagent.data.MemoryData;
import com.lzf.stackwatcher.insagent.data.NetworkData;

/**
* @author 李子帆
* @version 1.0
* @date 2018年12月12日 下午9:40:59
* @Description 类说明
*/
public class StandardMonitorService extends ContainerBase<Agent> implements MonitorService {

	private static final Logger log = Logger.getLogger(StandardMonitorService.class);
	
	private Agent agent;
	private TransferService transferService;
	
	private long networkDataCacheTime;
	private Map<String, NetInterfaceStat> networkDataCache;
	private Map<String, NetInterfaceConfig> networkConfigCache;
	
	private long diskDataCacheTime;
	private Map<String, FileSystemUsage> diskUsageDataCache;
	
	private ScheduledExecutorService executor;
	
	
	public StandardMonitorService(Agent agent) {
		this.agent = agent;
		setName("MonitorService");
		addLifecycleEventListener(LifecycleLoggerListener.INSTANCE);
	}

	@Override
	public Agent getParent() {
		return agent;
	}
	
	@Override
	protected void initInternal() {
		transferService = agent.getService(TransferService.SERVICE_NAME, TransferService.class);
		executor = new ScheduledThreadPoolExecutor(4);
		
		networkDataCache = new ConcurrentHashMap<>();
		networkConfigCache = new ConcurrentHashMap<>();
		try {
			initNetworkData();
		} catch (SigarException e) {
			log.error("无法获取网卡监控数据", e);
			System.exit(1);
		}

		diskUsageDataCache = new ConcurrentHashMap<>();
		
		try {
			initDiskData();
		} catch (SigarException e) {
			log.error("无法获取磁盘监控数据", e);
			System.exit(1);
		}
	}
	
	@Override
	protected void startInternal() {
		executor.scheduleAtFixedRate(cpuDataMonitorTask, 10, 15, TimeUnit.SECONDS);
		executor.scheduleAtFixedRate(memoryDataMonitorTask, 10, 15, TimeUnit.SECONDS);
		executor.scheduleAtFixedRate(networkDataMonitorTask, 10, 15, TimeUnit.SECONDS);
		executor.scheduleAtFixedRate(diskDataMonitorTask, 10, 15, TimeUnit.SECONDS);
	}
	
	private final Runnable cpuDataMonitorTask = new Runnable() {
		@Override
		public void run() {
			Data data = currentCPUData();
			transferService.transferMointorData(data);
		}
	};
	
	private final Runnable memoryDataMonitorTask = new Runnable() {
		
		@Override
		public void run() {
			Data data = currentMemoryMointorData();
			transferService.transferMointorData(data);
		}
	};
	
	private final Runnable networkDataMonitorTask = new Runnable() {	
		@Override
		public void run() {
			Data data = currentNetworkData();
			transferService.transferMointorData(data);
		}
	};
	
	private final Runnable diskDataMonitorTask = new Runnable() {
		@Override
		public void run() {
			Data data = currentDiskData();
			transferService.transferMointorData(data);
		}
	};

	@Override
	public CPUData currentCPUData() {
		Sigar sigar = new Sigar();
		try {
			CpuPerc[] cpuPercs = sigar.getCpuPercList();
			double idle = 0, system = 0, iowait = 0, user = 0, other = 0, totalUsed = 0;
			int len = cpuPercs.length;
			for(CpuPerc cpuPerc : cpuPercs) {
				double t0 = 0, t1 = 0, t2 = 0, t3 = 0, t4 = 0;
				idle += (t0 = cpuPerc.getIdle());
				system += (t1 = cpuPerc.getSys());
				iowait += (t2 = cpuPerc.getWait());
				user += (t3 = cpuPerc.getUser());
				other += (t4 = cpuPerc.getNice() + cpuPerc.getSoftIrq() + cpuPerc.getIrq() 
							+ cpuPerc.getStolen());
				totalUsed += (t0 + t1 + t2 + t3 + t4);
			}
			CPUData data = new CPUData(idle / len, system / len, iowait / len, user / len, 
									   other / len, totalUsed / len);
			return data;
		} catch (SigarException e) {
			log.error("获取CPU信息失败", e);
			return null;
		}
	}

	@Override
	public MemoryData currentMemoryMointorData() {
		Sigar sigar = new Sigar();
		try {
			Mem mem = sigar.getMem();
			MemoryData data = new MemoryData(mem.getTotal(), mem.getUsed(), mem.getActualUsed(), mem.getFree());
			return data;
		} catch (SigarException e) {
			log.error("获取内存信息失败", e);
			return null;
		}
	}

	@Override
	public NetworkData currentNetworkData() {
		Sigar sigar = new Sigar();
		try {
			String[] deviceName = sigar.getNetInterfaceList();
			List<NetworkData.Device> list = new ArrayList<>();
			for(int i = 0; i < deviceName.length; i++) {
				String device;
				NetInterfaceStat os = networkDataCache.get(device = deviceName[i]);
				if(os == null) {
					initNetworkData();
					try {
						Thread.sleep(500);
					} catch (InterruptedException ignore) {
						// NOOP
					}
				}
				NetInterfaceStat s = sigar.getNetInterfaceStat(device);
				NetInterfaceConfig c = networkConfigCache.get(device);
				long dt = System.currentTimeMillis() - networkDataCacheTime;
				NetworkData.Device d = new NetworkData.Device(device, c.getAddress(),
						(int)((s.getRxBytes() - os.getRxBytes()) / (double)dt * 1000.0), 
						(int)((s.getRxPackets() - os.getRxPackets()) / (double)dt * 1000.0), 
						(int)((s.getRxErrors() - os.getRxErrors()) / (double)dt * 1000.0),
						(int)((s.getTxBytes() - os.getTxBytes()) / (double)dt * 1000.0),
						(int)((s.getTxPackets() - os.getTxPackets() / (double)dt * 1000.0)), 
						(int)((s.getTxErrors() - os.getTxErrors()) / (double)dt * 1000.0));
				list.add(d);
			}
			
			return new NetworkData(list);
		} catch (SigarException e) {
			log.error("无法获取网卡数据", e);
			return null;
		}
	}
	
	/**
	 * 初始化网络监控数据缓存，便于算出网卡流量速率
	 * @throws SigarException
	 */
	private void initNetworkData() throws SigarException {
		networkDataCache.clear();
		Sigar sigar = new Sigar();
		String[] deviceName = sigar.getNetInterfaceList();
		long st = System.currentTimeMillis();
		for(int i = 0; i < deviceName.length; i++) {
			String d = deviceName[i];
			NetInterfaceStat s = sigar.getNetInterfaceStat(d);
			NetInterfaceConfig c = sigar.getNetInterfaceConfig(d);
			networkDataCache.put(d, s);
			networkConfigCache.put(d, c);
		}
		networkDataCacheTime = (System.currentTimeMillis() + st) / 2;
	}

	@Override
	public DiskData currentDiskData() {
		Sigar sigar = new Sigar();
		try {
			FileSystem[] fsarr = sigar.getFileSystemList();
			DiskData.Device[] devices = new DiskData.Device[fsarr.length];
			int ls = 0, index = 0;
			
			for(FileSystem fs : fsarr) {
				if(fs.getType() != 2) {
					ls++;
					continue;
				}
				String devName, dirName;
				FileSystemUsage ofsu = diskUsageDataCache.get(devName = fs.getDevName());
				FileSystemUsage fsu = sigar.getFileSystemUsage(dirName = fs.getDirName());
				long dt = System.currentTimeMillis() - diskDataCacheTime;
				DiskData.Device data = new DiskData.Device(devName, dirName, fsu.getUsed(), fsu.getFree(), fsu.getTotal(),
						(long)((double)(fsu.getDiskReadBytes() - ofsu.getDiskReadBytes()) / dt * 1000.0),
						(long)((double)(fsu.getDiskReads() - ofsu.getDiskReads()) / dt * 1000.0),
						(long)((double)(fsu.getDiskWriteBytes() - ofsu.getDiskWriteBytes()) / dt * 1000.0),
						(long)((double)(fsu.getDiskWrites() - ofsu.getDiskWrites()) / dt * 1000.0));
				devices[index++] = data;
			}
			
			if(ls > 0) {
				int oldLen = devices.length;
				int newLen = oldLen - ls;
				DiskData.Device[] narr = new DiskData.Device[newLen];
				for(int i = 0, j = 0; i < oldLen; i++) {
					if(devices[i] != null) {
						narr[j++] = devices[i];
					}
				}
			}
			
			DiskData diskData = new DiskData(devices);
			return diskData;
		} catch (SigarException e) {
			log.error("无法获取磁盘数据", e);
			return null;
		}
	}
	
	private void initDiskData() throws SigarException {
		diskUsageDataCache.clear();
		Sigar sigar = new Sigar();
		FileSystem[] fs = sigar.getFileSystemList();
		long st = System.currentTimeMillis();
		for(int i = 0; i < fs.length; i++) {
			FileSystem f = fs[i];
			if(f.getType() == 2) {
				FileSystemUsage fsu = sigar.getFileSystemUsage(f.getDirName());
				diskUsageDataCache.put(f.getDevName(), fsu);
			}
		}
		diskDataCacheTime = (System.currentTimeMillis() + st) / 2;
	}
}
