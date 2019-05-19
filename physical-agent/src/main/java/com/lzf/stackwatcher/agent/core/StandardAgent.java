package com.lzf.stackwatcher.agent.core;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.agent.*;
import com.lzf.stackwatcher.agent.Service;
import com.lzf.stackwatcher.agent.data.InstanceData;
import com.lzf.stackwatcher.agent.data.NovaData;
import com.lzf.stackwatcher.agent.data.StoragePoolData;
import com.lzf.stackwatcher.common.*;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;
import com.lzf.stackwatcher.zookeeper.ZooKeeperConnector;
import org.apache.log4j.Logger;

/**
 * @author 李子帆
 * @time 2018年11月20日 下午2:20:23
 */
public class StandardAgent extends ContainerBase<Void> implements Agent {
	
	private static final Logger log = Logger.getLogger(StandardAgent.class);

	private Map<String, Service<?>> serviceMap = new ConcurrentHashMap<>();

	private ConfigManager configManager = new DefaultConfigManager("ConfigManager");

	private ZooKeeper zooKeeper;

	private ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);

	private boolean createNode = false;
	
	public StandardAgent() {
		setName("Agent");
		addLifecycleEventListener(LifecycleLoggerListener.INSTANCE);

		configManager.registerConfig(new ZooKeeperConfig(this));
	}
	
	@Override
	protected void initInternal() {
		zooKeeper = new ZooKeeperConnector(this);
		GlobalConfig cfg;
		configManager.registerConfig(cfg = new GlobalConfig(this, zooKeeper));

		DomainManagerService dms = new StandardDomainManagerService(this);
		serviceMap.put(dms.serviceName(), dms);

		RedisService rs = new StandardRedisService(this);
		serviceMap.put(rs.serviceName(), rs);
		TransferService ts = new StandardTransferService(this);
		serviceMap.put(ts.serviceName(), ts);

		for(Map.Entry<String, Service<?>> entry : serviceMap.entrySet()) {
			entry.getValue().init();
		}

		if(cfg.isRemoteConfig()) {
			try {
				zooKeeper.registerWatcher(cfg.getZNodePath(), e -> {
					log.info("Detected config ZNode \"" + cfg.getZNodePath() + "\" changed, ready to restart agent");
					restart();
				});
			} catch (Exception e) {
				throw new IllegalStateException("Could not register ZooKeeper watcher at ZNode " + cfg.getZNodePath(), e);
			}
		}
	}
	
	@Override
	protected void startInternal() {
		for(Map.Entry<String, Service<?>> entry : serviceMap.entrySet()) {
			entry.getValue().start();
		}

		executor.scheduleAtFixedRate(() -> {
			// 设置ZNode
			DomainManagerService dms = getService(DomainManagerService.DEFAULT_SERVICE_NAME, DomainManagerService.class);
			MonitorService ms = dms.getMonitorService();
			MonitorService.Config cfg = getConfig(MonitorService.DEFAULT_CONFIG_NAME, MonitorService.Config.class);

			JSONObject obj = new JSONObject();
			obj.put("host", dms.getHostName());

			JSONArray insArr = new JSONArray();
			insArr.add(dms.getAllInstanceUUID());
			obj.put("instances", insArr);

			obj.put("instance-monitor-enable", cfg.enable());
			if (cfg.enable()) {
				JSONObject ins = new JSONObject();
				ins.put("cpu", cfg.insVCPUMonitorRate());
				ins.put("network", cfg.insNetworkIOMonitorRate());
				ins.put("disk-io", cfg.insDiskIOMonitorRate());
				ins.put("disk-capacity", cfg.insDiskCapacityMonitorRate());
				obj.put("instance-monitor-rate", ins);
			}

			JSONObject nova = new JSONObject();
			nova.put("cpu", cfg.novaCPUMonitorRate());
			nova.put("memory", cfg.novaRAMMonitorRate());
			nova.put("network", cfg.novaNetworkIOMonitorRate());
			nova.put("disk-io", cfg.novaDiskIOMonitorRate());
			nova.put("disk-capacity", cfg.novaDiskCapacityMonitorRate());
			obj.put("nova-monitor-rate", nova);

			obj.put("instance-agent-port", cfg.insAgentRecivePort());
			obj.put("nova-info", ms.novaInfo());
			obj.put("instance-info", ms.currentAllInstanceInfo());
			obj.put("storage-pool-info", ms.currentStoragePoolData());

			if(!createNode) {
				try {
					zooKeeper.createTemporaryNodeRecursive("/stackwatcher/agent/" + dms.getHostName(), obj.toJSONString().getBytes(Charset.forName("UTF-8")));
					createNode = true;
				} catch (Exception e) {
					log.error("Can not create ZNode at path /stackwatcher/agent/" + dms.getHostName());
				}
			} else {
				try {
					zooKeeper.updateNode("/stackwatcher/agent/" + dms.getHostName(), obj.toJSONString().getBytes(Charset.forName("UTF-8")));
				} catch (Exception e) {
					log.error("Can not update ZNode at path /stackwatcher/agent/" + dms.getHostName());
				}
			}
		}, 10, 300, TimeUnit.SECONDS);
	}
	
	@Override
	protected void stopInternal() {
		for(Map.Entry<String, Service<?>> entry : serviceMap.entrySet()) {
			entry.getValue().stop();
		}

		executor.shutdownNow();
		zooKeeper.close();
	}
	
	@Override
	protected void restartInternal() {
		for(Map.Entry<String, Service<?>> entry : serviceMap.entrySet()) {
			entry.getValue().restart();
		}

		init();
		start();
	}
	
	@Override
	public Service<?> getService(String serviceName) {
		return serviceMap.get(serviceName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Service<?>> T getService(String serviceName, Class<T> serviceClass) {
		return (T) serviceMap.get(serviceName);
	}

	
	@Override
	public void addService(Service<?> service) {
		serviceMap.put(service.serviceName(), service);
	}

	@Override
	public ZooKeeper getZooKeeper() {
		return zooKeeper;
	}

	@Override
	public InputStream loadResource(String path) throws Exception {
		return configManager.loadResource(path);
	}

	@Override
	public void registerConfig(Config config) {
		configManager.registerConfig(config);
	}

	@Override
	public void updateConfig(Config config) {
		configManager.updateConfig(config);
	}

	@Override
	public void removeConfig(Config config) {
		configManager.removeConfig(config);
	}

	@Override
	public boolean saveConfig(Config config) {
		return configManager.saveConfig(config);
	}

	@Override
	public Config getConfig(String name) {
		return configManager.getConfig(name);
	}

	@Override
	public <C extends Config> C getConfig(String name, Class<C> requireType) {
		return configManager.getConfig(name, requireType);
	}

	@Override
	public void registerConfigEventListener(ConfigEventListener listener) {
		configManager.registerConfigEventListener(listener);
	}

	@Override
	public void removeConfigEventListener(ConfigEventListener listener) {
		configManager.removeConfigEventListener(listener);
	}
}
