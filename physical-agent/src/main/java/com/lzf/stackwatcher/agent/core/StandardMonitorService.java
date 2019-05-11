package com.lzf.stackwatcher.agent.core;

import java.nio.charset.Charset;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static java.lang.String.format;

import org.apache.log4j.Logger;
import org.libvirt.DomainBlockInfo;
import org.libvirt.DomainBlockStats;
import org.libvirt.DomainInfo;

import org.libvirt.DomainInterfaceStats;
import org.libvirt.LibvirtException;
import org.libvirt.VcpuInfo;

import com.alibaba.fastjson.JSONObject;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.concurrent.Future;

import com.lzf.stackwatcher.common.ContainerBase;

import com.lzf.stackwatcher.agent.DomainManagerService;
import com.lzf.stackwatcher.agent.MonitorService;
import com.lzf.stackwatcher.agent.RedisService;
import com.lzf.stackwatcher.agent.TransferService;
import com.lzf.stackwatcher.agent.core.LibvirtDomainAdaptor.DiskInfo;
import com.lzf.stackwatcher.agent.core.LibvirtDomainAdaptor.NetworkInterface;
import com.lzf.stackwatcher.agent.data.BaseInstanceData;
import com.lzf.stackwatcher.agent.data.InstanceDiskCapacityData;
import com.lzf.stackwatcher.agent.data.InstanceDiskIOData;
import com.lzf.stackwatcher.agent.data.InstanceData;
import com.lzf.stackwatcher.agent.data.InstanceNetworkIOData;
import com.lzf.stackwatcher.agent.data.InstanceRAMData;
import com.lzf.stackwatcher.agent.data.InstanceVCPUData;
import com.lzf.stackwatcher.agent.data.NovaCPUData;
import com.lzf.stackwatcher.agent.data.NovaData;
import com.lzf.stackwatcher.agent.data.NovaDiskCapacityData;
import com.lzf.stackwatcher.agent.data.NovaDiskIOData;
import com.lzf.stackwatcher.agent.data.NovaNetworkIOData;
import com.lzf.stackwatcher.agent.data.NovaRAMData;

/**
* @author 李子帆
* @version 1.0
* @date 2018年11月20日 下午6:15:11
* @Description 计算节点、云主机基本监控数据采集器
*/

public class StandardMonitorService extends ContainerBase<DomainManagerService> implements MonitorService {
	
	private static final Logger log = Logger.getLogger(StandardMonitorService.class);
	
	//Redis缓存键名前缀
	static final String PREFIX_CPU = "cpu_";
	static final String PREFIX_RAM = "ram_";
	static final String PREFIX_NETWORK_IO = "net_";
	static final String PREFIX_DISK_IO = "dio_";
	static final String PREFIX_DISK_CAP = "dcap_";
	
	static final String NOVA_CPU = "cpu";
	static final String NOVA_RAM = "ram";
	static final String NOVA_NETWORK_IO = "net";
	static final String NOVA_DISK_IO = "dio";
	static final String NOVA_DISK_CAP = "dcap";
	
	private StandardDomainManagerService domainManagerService;

	private RedisService redisService;

	private TransferService transferService;
	
	private EventLoopGroup bossGroup;
	
	private EventLoopGroup workerGroup;

	public StandardMonitorService(StandardDomainManagerService domainManagerService) {
		setName("MointorService");
		addLifecycleEventListener(LifecycleLoggerListener.INSTANCE);
		this.domainManagerService = domainManagerService;

		getParent().getParent().registerConfig(new MonitorConfig(getParent().getParent()));
	}

	@Override
	protected void initInternal() {
		redisService = getParent().getParent().getService(RedisService.DEFAULT_SERVICE_NAME, RedisService.class);
		transferService = getParent().getParent().getService(TransferService.DEFAULT_SERVICE_NAME, TransferService.class);
		initNettyServer();
	}
	
	@Override
	protected void startInternal() {
		MonitorService.Config c = domainManagerService.getParent().getConfig(DEFAULT_CONFIG_NAME,
				MonitorService.Config.class);
		
		log.info(format("[组件:%s] 配置:%s", getName(), c.toString()));
		
		int insVCPU, insRAM, insNetio, insDiskio, insDiskcap;
		int novaCPU, novaRAM, novaNetio, novaDiskio, novaDiskcap;
		
		ScheduledExecutorService executor = domainManagerService.executor;
		
		if((insVCPU = c.insVCPUMonitorRate()) != -1)
			executor.scheduleAtFixedRate(INS_VCPU_MONITOR_RUNNABLE, 5, insVCPU, TimeUnit.SECONDS);
		if((insRAM = c.insRAMMonitorRate()) != -1)
			executor.scheduleAtFixedRate(INS_RAM_MONITOR_RUNNABLE, 5, insRAM, TimeUnit.SECONDS);
		if((insNetio = c.insNetworkIOMonitorRate()) != -1)
			executor.scheduleAtFixedRate(INS_NETWORK_IO_RUNNABLE, 5, insNetio, TimeUnit.SECONDS);
		if((insDiskio = c.insDiskIOMonitorRate()) != -1)
			executor.scheduleAtFixedRate(INS_DISK_IO_RUNNABLE, 5, insDiskio, TimeUnit.SECONDS);
		if((insDiskcap = c.insDiskCapacityMonitorRate()) != -1)
			executor.scheduleAtFixedRate(INS_DISK_SIZE_RUNNABLE, 5, insDiskcap, TimeUnit.SECONDS);
		
		executor.scheduleAtFixedRate(DOMAIN_STATUS_RUNNABLE, 5, 60, TimeUnit.SECONDS);
		
		if((novaCPU = c.novaCPUMonitorRate()) != -1)
			executor.scheduleAtFixedRate(NOVA_CPU_MOINTOR_TASK, 5, novaCPU, TimeUnit.SECONDS);
		if((novaRAM = c.novaRAMMonitorRate()) != -1)
			executor.scheduleAtFixedRate(NOVA_RAM_MOINTOR_TASK, 5, novaRAM, TimeUnit.SECONDS);
		if((novaNetio = c.novaNetworkIOMonitorRate()) != -1)
			executor.scheduleAtFixedRate(NOVA_NETWORK_IO_MOINTOR_TASK, 5, novaNetio, TimeUnit.SECONDS);
		if((novaDiskio = c.novaDiskIOMonitorRate()) != -1)
			executor.scheduleAtFixedRate(NOVA_DISKIO_MOINTOR_TASK, 5, novaDiskio, TimeUnit.SECONDS);
		if((novaDiskcap = c.novaDiskCapacityMonitorRate()) != -1)
			executor.scheduleAtFixedRate(NOVA_DISKCAP_MOINTOR_TASK, 5, novaDiskcap, TimeUnit.SECONDS);
	}
	
	@Override
	protected void stopInternal() {
		try {
			domainManagerService.libvirtConn.close();
		} catch (LibvirtException e) {
			log.warn("Libvirt连接关闭失败", e);
		}
		closeNettyServer();
		domainManagerService.executor.shutdown();
	}
	
	@Override
	public DomainManagerService getParent() {
		return domainManagerService;
	}
	
	/**
	 * 初始化Netty服务器，负责接收来自云主机Instance Agent的监控数据，监控数据包结构如下：
	 * 示例：(首部字段1字节，表示监控数据类型，长度字段2字节)
	 * +----------+----------+----------------+
	 * | Header 1 |  Length  | Actual Content |
	 * |   0x01   |  0x000C  | "HELLO, WORLD" |
	 * +----------+----------+----------------+
	 * 
	 */
	private void initNettyServer() {
		MonitorService.Config c = domainManagerService.getParent().getConfig(DEFAULT_CONFIG_NAME,
				MonitorService.Config.class);
		if(c.insAgentRecivePort() == -1)
			return;

		log.info("虚拟机Agent监控数据采集模块启动中...");
		int ts = Runtime.getRuntime().availableProcessors();
		int port = c.insAgentRecivePort();
		ts = ts >= 8 ? 8 : ts;
		log.info(format("准备绑定端口:%d", port));
		ServerBootstrap boot = new ServerBootstrap();
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup(ts);
		boot.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) {
					//最大包长度为2KB，包首部字段1字节，长度字段2字节
					ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(32768, 1, 2, 0, 0),
							instanceAgentMointorDataInboundHandler);
				}
			});
		try {
			boot.bind(port).addListener(f -> {
				if (f.isSuccess()) {
					log.info("虚拟机Agent监控数据采集模块启动成功");
				} else {
					log.error("虚拟机Agent监控数据采集模块启动失败");
					System.exit(1);
				}
			}).await();
		} catch (InterruptedException ignore) {
		}
	}
	
	/**
	 * 关闭Instance Agent数据接收模块
	 */
	private void closeNettyServer() {
		Future<?> f0 = bossGroup.shutdownGracefully();
		Future<?> f1 = workerGroup.shutdownGracefully();
		try {
			f0.sync();
			f1.sync();
		} catch (InterruptedException ignore) {
		}
	}
	
	
	
	/**
	 * Netty入站处理器
	 * 负责接收云主机Agent程序发送的监控数据包并将其转发给RabbitMQ消息队列服务
	 **/
	private final class InstanceAgentMonitorDataInboundHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			if(msg instanceof ByteBuf) {
				ByteBuf buf = (ByteBuf) msg;
				byte type = buf.readByte();
				short len = buf.readShort();
				byte[] data = new byte[len];
				buf.readBytes(data);
				String json = new String(data, Charset.forName("UTF-8"));
				TransferService.InstanceAgentDataType t = TransferService.InstanceAgentDataType.getByByteType(type);
				transferService.transferInstanceAgentData(t, addNovaHostName(json));
			}
		}
		
		/**
		 * 向Instance Agent的监控数据添加当前物理机主机名
		 * @param json JSON字符串
		 * @return 添加后的JSON字符串
		 */
		private String addNovaHostName(String json) {
			JSONObject object = JSONObject.parseObject(json);
			object.put("host", domainManagerService.getHostName());
			return object.toJSONString();
		}
	}
	
	
	
	private final InstanceAgentMonitorDataInboundHandler instanceAgentMointorDataInboundHandler =
				new InstanceAgentMonitorDataInboundHandler();
	
	/**
	 * 负责监控CPU使用率，用于定时任务定期执行
	 */
	private final Runnable INS_VCPU_MONITOR_RUNNABLE = new Runnable() {
		@Override
		public void run() {
			try {
				int size = domainManagerService.runningInstanceAdaptors.size();
				VcpuInfo[][] sviArr = new VcpuInfo[size][];
				long[] stArr = new long[size];
				
				//第一次获取所有处于运行状态的虚拟机的CPU时间
				int i = 0;
				for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
					VcpuInfo[] cinf = adaptor.currentCPUStates();
					if(cinf == null) {
						i++;
						continue;
					}
					stArr[i] = System.currentTimeMillis();
					sviArr[i++] = cinf;
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.info("CPU监控任务线程被中断", e);
					return;
				}
				
				InstanceVCPUData[] data = new InstanceVCPUData[size];
				
				//第二次获取所有处于运行状态的虚拟机的CPU时间，并计算出CPU使用率(包含每个CPU核心)
				i = 0;
				for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
					//获取当前实例的CPU运行情况，每个VcpuInfo对应该虚拟机持有的一个CPU核心
					VcpuInfo[] cinf = adaptor.currentCPUStates();
					if(cinf == null) {
						i++;
						continue;
					}
					long d = System.currentTimeMillis() - stArr[i];
					int len;
					InstanceVCPUData.VCPU[] vcpus = new InstanceVCPUData.VCPU[len = cinf.length];
					//计算每个CPU核心的使用率
					for(int j = 0; j < len; j++) {
						VcpuInfo inf = cinf[j];
						double u = (double)(cinf[j].cpuTime - sviArr[i][j].cpuTime) / (d * 10_000);
						vcpus[j] = new InstanceVCPUData.VCPU(inf.number, inf.state.ordinal(), u);
					}
					//存储监控数据
					data[i] = new InstanceVCPUData(domainManagerService.getHostName(), 
							adaptor.id, adaptor.uuid, adaptor.name, vcpus);
					i++;
				}
				
				for(InstanceVCPUData dt : data)
					redisService.insertList(PREFIX_CPU + dt.uuid, dt.toJSON());
			} catch (Exception e) {
				log.warn("云主机CPU监控线程发生异常", e);
			}
		}
	};
	
	/**
	 * 负责监控虚拟机的内存信息
	 */
	private final Runnable INS_RAM_MONITOR_RUNNABLE = new Runnable() {
		@Override
		public void run() {
			try {
				for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
					InstanceRAMData data = parseMemoryStatisticToData(adaptor);
					redisService.insertList(PREFIX_RAM + adaptor.uuid, data.toJSON());
				}
			} catch (Exception e) {
				log.warn("云主机内存监控线程发生异常", e);
			}
		}
	};
	
	/**
	 * 负责监控虚拟机的网络IO
	 */
	private final Runnable INS_NETWORK_IO_RUNNABLE = new Runnable() {
		@Override
		public void run() {
			try {
				for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
					InstanceNetworkIOData data = parseInterfaceStatsToData(adaptor);
					redisService.insertList(PREFIX_NETWORK_IO + adaptor.uuid, data.toJSON());
				}
			} catch (Exception e) {
				log.warn("云主机网络IO监控线程发生异常", e);
			}
		}
	};
	
	/**
	 * 负责监控虚拟机的磁盘IO
	 */
	private final Runnable INS_DISK_IO_RUNNABLE = new Runnable() {
		@Override
		public void run() {
			try {
				for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
					InstanceDiskIOData data = parseBlockStatsToData(adaptor);
					redisService.insertList(PREFIX_DISK_IO + adaptor.uuid, data.toJSON());
				}
			} catch (Exception e) {
				log.warn("云主机磁盘IO监控线程发生异常", e);
			}
		}
	};
	
	/**
	 * 负责监控虚拟机的磁盘容量信息
	 */
	private final Runnable INS_DISK_SIZE_RUNNABLE = new Runnable() {
		@Override
		public void run() {
			try {
				for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
					InstanceDiskCapacityData data = parseBlockInfoToData(adaptor);
					redisService.insertList(PREFIX_DISK_CAP + adaptor.uuid, data.toJSON());
				}
			} catch (Exception e) {
				log.warn("云主机磁盘容量监控线程发生异常", e);
			}
		}
	};
	
	/**
	 * 负责监控虚拟机状态
	 */
	private final Runnable DOMAIN_STATUS_RUNNABLE = new Runnable() {
		private int times = 1;
		@Override
		public void run() {
			try {
				if(times % 10 != 0) {
					domainManagerService.checkInstanceStates(false);
					times++;
				} else {
					domainManagerService.checkInstanceStates(true);
					times = 1;
				}
			} catch (Exception e) {
				log.error("云主机状态监控线程发生异常", e);
			}
		}
	};
	
	private final Runnable NOVA_CPU_MOINTOR_TASK = new Runnable() {
		@Override
		public void run() {
			try {
				NovaCPUData data = currentNovaCPUUsage();
				redisService.insertList(NOVA_CPU, data.toJSON());
			} catch (Exception e) {
				log.error("计算节点CPU监控线程发生异常", e);
			}
		}
	};
	
	private final Runnable NOVA_RAM_MOINTOR_TASK = new Runnable() {	
		@Override
		public void run() {
			try {
				NovaRAMData data = currentNovaRAMUsage();
				redisService.insertList(NOVA_RAM, data.toJSON());
			} catch (Exception e) {
				log.error("计算节点内存监控线程发生异常", e);
			}
		}
	};
	
	private final Runnable NOVA_NETWORK_IO_MOINTOR_TASK = new Runnable() {
		@Override
		public void run() {
			try {
				NovaNetworkIOData data = currentNovaNetworkIO();
				redisService.insertList(NOVA_NETWORK_IO, data.toJSON());
			} catch (Exception e) {
				log.error("计算节点网络IO监控线程发生异常", e);
			}
		}
	};
	
	private final Runnable NOVA_DISKIO_MOINTOR_TASK = new Runnable() {		
		@Override
		public void run() {
			try {
				NovaDiskIOData data = currentNovaDiskIO();
				redisService.insertList(NOVA_DISK_IO, data.toJSON());
			} catch (Exception e) {
				log.error("计算节点磁盘IO监控线程发生异常", e);
			}
		}
	};
	
	private final Runnable NOVA_DISKCAP_MOINTOR_TASK = new Runnable() {
		@Override
		public void run() {
			try {
				NovaDiskCapacityData data = currentNovaDiskInfo();
				redisService.insertList(NOVA_DISK_CAP, data.toJSON());
			} catch (Exception e) {
				log.error("计算节点磁盘容量监控线程发生异常", e);
			}
			
		}
	};
	
	@Override
	public InstanceNetworkIOData currentInstanceNetworkIO(String uuid) {
		for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
			if(adaptor.uuid.equals(uuid)) {
				return parseInterfaceStatsToData(adaptor);
			}
		}
		return null;
	}
	
	@Override
	public InstanceNetworkIOData[] currentAllInstanceNetworkIO() {
		InstanceNetworkIOData[] arr = new InstanceNetworkIOData[domainManagerService.runningInstanceAdaptors.size()];
		int i = 0;
		for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
			arr[i++] = parseInterfaceStatsToData(adaptor);
		}
		return arr;
	}
	
	/**
	 * 根据对应云主机的Domain接口类获取云主机的网络IO信息
	 * @param adaptor 云主机的接口类对象
	 * @return 云主机的磁盘IO信息
	 */
	private InstanceNetworkIOData parseInterfaceStatsToData(LibvirtDomainAdaptor adaptor) {
		if(adaptor.interfaceCache == null) {
			adaptor.currentNetworkIO();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignore) {
			}
		}
		
		long stTime = adaptor.interfaceCacheTime;
		DomainInterfaceStats[] oldStats = adaptor.interfaceCache;
		DomainInterfaceStats[] stats = adaptor.currentNetworkIO();
		String[] device = new String[stats.length];
		String[] uuids = new String[stats.length];
		int j = 0;
		for(NetworkInterface ni : adaptor.networkInterfaces) {
			device[j] = ni.source;
			uuids[j++] = ni.uuid;
		}
		return new InstanceNetworkIOData(domainManagerService.getHostName(),adaptor.id, adaptor.uuid,
				adaptor.name, device, uuids, stats, oldStats, System.currentTimeMillis() - stTime);
	}

	@Override
	public InstanceDiskCapacityData currentInstanceDiskInfo(String uuid) {
		for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
			if(adaptor.uuid.equals(uuid)) {
				return parseBlockInfoToData(adaptor);
			}
		}
		return null;
	}
	
	@Override
	public InstanceDiskCapacityData[] currentAllInstanceDiskInfo() {
		InstanceDiskCapacityData[] arr = new InstanceDiskCapacityData[domainManagerService.runningInstanceAdaptors.size()];
		int i = 0;
		for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
			arr[i++] = parseBlockInfoToData(adaptor);
		}
		return arr;
	}
	
	/**
	 * 根据对应云主机的Domain接口类获取云主机的磁盘使用信息
	 * @param adaptor 云主机的接口类对象
	 * @return 云主机的磁盘使用信息
	 */
	private InstanceDiskCapacityData parseBlockInfoToData(LibvirtDomainAdaptor adaptor) {
		DomainBlockInfo[] arr = adaptor.currentDiskInfo();	
		String[] device = new String[arr.length];
		int i = 0;
		for(DiskInfo di : adaptor.diskinfos)
			device[i++] = di.diskDevice;
		return new InstanceDiskCapacityData(domainManagerService.getHostName(), adaptor.id, 
				adaptor.uuid, adaptor.name, device, arr);
	}
	
	@Override
	public InstanceDiskIOData currentInstanceDiskIO(String uuid) {
		for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
			if(adaptor.uuid.equals(uuid)) {
				return parseBlockStatsToData(adaptor);
			}
		}
		return null;
	}
	
	@Override
	public InstanceDiskIOData[] currentAllInstanceDiskIO() {
		InstanceDiskIOData[] arr = new InstanceDiskIOData[domainManagerService.runningInstanceAdaptors.size()];
		int i = 0;
		for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
			arr[i++] = parseBlockStatsToData(adaptor);
		}
		return arr;
	}
	
	/**
	 * 根据对应云主机的Domain接口类获取云主机的磁盘IO信息
	 * @param adaptor 云主机的接口类对象
	 * @return 云主机的磁盘IO信息
	 */
	private InstanceDiskIOData parseBlockStatsToData(LibvirtDomainAdaptor adaptor) {
		DomainBlockStats[] oldArr = adaptor.blockStatsCache;
		long stTime = adaptor.interfaceCacheTime;
		DomainBlockStats[] arr = adaptor.currentDiskIO();
		String[] device = new String[arr.length];
		int i = 0;
		for(DiskInfo di : adaptor.diskinfos)
			device[i++] = di.diskDevice;
		return new InstanceDiskIOData(domainManagerService.getHostName(), adaptor.id, adaptor.uuid, 
				adaptor.name, device, oldArr, arr, System.currentTimeMillis() - stTime);
	}

	@Override
	public InstanceVCPUData currentInstanceVCPUUsage(String uuid) {
		for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
			if(adaptor.uuid.equals(uuid)) {
				return parseVcpuInfoToData(adaptor);
			}
		}
		return null;
	}
	
	@Override
	public InstanceVCPUData[] currentAllInstanceVCPUUsage() {
		InstanceVCPUData[] arr = new InstanceVCPUData[domainManagerService.runningInstanceAdaptors.size()];
		int i = 0;
		for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
			arr[i++] = parseVcpuInfoToData(adaptor);
		}
		return arr;
	}
	
	/**
	 * 根据对应云主机的Domain接口类获取云主机的CPU使用信息
	 * @param adaptor 云主机的接口类对象
	 * @return 云主机的CPU使用信息
	 */
	private InstanceVCPUData parseVcpuInfoToData(LibvirtDomainAdaptor adaptor) {
		VcpuInfo[] stInf = adaptor.currentCPUStates();
		long st = System.currentTimeMillis();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			log.warn(String.format("[虚拟机UUID:%s] 获取VCPU使用数据失败"), e);
			return null;
		}
		VcpuInfo[] edInf = adaptor.currentCPUStates();
		long ed = System.currentTimeMillis();
		long d = ed - st;
		
		int len;
		InstanceVCPUData.VCPU[] arr = new InstanceVCPUData.VCPU[len = stInf.length];
		for(int i = 0; i < len; i++) {
			VcpuInfo sf = stInf[i];
			VcpuInfo ef = edInf[i];
			double usage = (double)(ef.cpuTime - sf.cpuTime) / (d * 10_000);
			arr[i] = new InstanceVCPUData.VCPU(sf.number, sf.state.ordinal(), usage);
		}
		
		return new InstanceVCPUData(domainManagerService.getHostName(), adaptor.id, adaptor.uuid, adaptor.name, arr);
	}

	@Override
	public InstanceData currentInstanceInfo(String uuid) {
		for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
			if(adaptor.uuid.equals(uuid)) {
				return parseDomainInfoToInstanceData(adaptor);
			}
		}
		return null;
	}

	@Override
	public InstanceData[] currentAllInstanceInfo() {
		InstanceData[] arr = new InstanceData[domainManagerService.runningInstanceAdaptors.size()];
		int i = 0;
		for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
			arr[i++] = parseDomainInfoToInstanceData(adaptor);
		}
		return arr;
	}

	/**
	 * 根据对应云主机的Domain接口类获取实例信息
	 * @param adaptor 云主机接口类
	 * @return 云主机信息
	 */
	private InstanceData parseDomainInfoToInstanceData(LibvirtDomainAdaptor adaptor) {
		DomainInfo domainInfo = adaptor.currentDomainState();
		int len1, len2;
		InstanceData.NetworkInterfaceData[] interfaceData = new InstanceData.NetworkInterfaceData[len1 = adaptor.networkInterfaces.length];
		InstanceData.DiskData[] diskData = new InstanceData.DiskData[len2 = adaptor.diskinfos.length];
		for(int i = 0; i < len1; i++) {
			NetworkInterface net = adaptor.networkInterfaces[i];
			interfaceData[i] = new InstanceData.NetworkInterfaceData(net.type, net.macAddr, net.bridge, net.netDevice, net.source);
		}
		for(int i = 0; i < len2; i++) {
			DiskInfo info = adaptor.diskinfos[i];
			diskData[i] = new InstanceData.DiskData(info.type, info.diskDevice);
		}
		
		return new InstanceData(domainManagerService.getHostName(), adaptor.id, adaptor.uuid, adaptor.name, domainInfo.state.ordinal(),
				adaptor.vmType, adaptor.osType, adaptor.memory, adaptor.curMemory, adaptor.vcpus,
				interfaceData, diskData);
	}

	@Override
	public InstanceRAMData currentInstanceRAMUsage(String uuid) {
		for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
			if(adaptor.uuid.equals(uuid)) {
				return parseMemoryStatisticToData(adaptor);
			}
		}
		return null;
	}

	@Override
	public InstanceRAMData[] currentAllInstanceRAMUsage() {
		InstanceRAMData[] arr = new InstanceRAMData[domainManagerService.runningInstanceAdaptors.size()];
		int i = 0;
		for(LibvirtDomainAdaptor adaptor : domainManagerService.runningInstanceAdaptors) {
			arr[i++] = parseMemoryStatisticToData(adaptor);
		}
		return arr;
	}
	
	/**
	 * 根据对应云主机的Domain接口类获取内存信息
	 * @param adaptor 云主机接口类
	 * @return 云主机内存使用信息
	 */
	private InstanceRAMData parseMemoryStatisticToData(LibvirtDomainAdaptor adaptor) {
		return new InstanceRAMData(domainManagerService.getHostName(), adaptor.id, adaptor.uuid,
				adaptor.name, adaptor.currentMemoryStatistic());
	}

	@Override
	public BaseInstanceData[] allInstanceBaseInfo() {
		BaseInstanceData[] data = new BaseInstanceData[domainManagerService.instanceAdaptors.size()];
		int i = 0;
		for(LibvirtDomainAdaptor adaptor : domainManagerService.instanceAdaptors) {
			data[i++] = new BaseInstanceData(domainManagerService.getHostName(), adaptor.id, 
					adaptor.uuid, adaptor.name);
		}
		return data;
	}

	@Override
	public NovaData novaInfo() {
		return domainManagerService.novaAdaptor.novaInfo;
	}

	@Override
	public NovaCPUData currentNovaCPUUsage() {
		return domainManagerService.novaAdaptor.currentCPUUsage();
	}

	@Override
	public NovaRAMData currentNovaRAMUsage() {
		return domainManagerService.novaAdaptor.currentRAMUsage();
	}

	@Override
	public NovaNetworkIOData currentNovaNetworkIO() {
		return domainManagerService.novaAdaptor.currentNetworkIOInfo();
	}

	@Override
	public NovaDiskIOData currentNovaDiskIO() {
		return domainManagerService.novaAdaptor.currentDiskIOInfo();
	}

	@Override
	public NovaDiskCapacityData currentNovaDiskInfo() {
		return domainManagerService.novaAdaptor.currentDiskUsage();
	}
}
