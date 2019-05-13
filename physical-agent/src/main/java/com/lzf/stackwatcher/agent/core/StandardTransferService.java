package com.lzf.stackwatcher.agent.core;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.lzf.stackwatcher.agent.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

import com.lzf.stackwatcher.common.ContainerBase;

import static java.lang.String.format;
/**
 * 
 * @author 李子帆
 * @time 2018年11月22日 上午10:21:03
 */
public class StandardTransferService extends ContainerBase<Agent> implements TransferService {
	
	private static final Logger log = Logger.getLogger(StandardTransferService.class);

	private final Agent agent;

	private KafkaProducer<String, String> kafka;
	
	private ScheduledExecutorService executor;
	
	private RedisService redisService;
	
	private BlockingQueue<String> instanceAgentCPUDataQueue;
	private BlockingQueue<String> instanceAgentMemoryDataQueue;
	private BlockingQueue<String> instanceAgentNetworkDataQueue;
	private BlockingQueue<String> instanceAgentDiskDataQueue;
	
	public StandardTransferService(Agent agent) {
		setName("TransferService");
		addLifecycleEventListener(LifecycleLoggerListener.INSTANCE);
		this.agent = agent;

		getParent().registerConfig(new TransferConfig(getParent(), getParent().getZooKeeper()));
	}

	@Override
	public Agent getParent() {
		return agent;
	}


	@Override
	protected void initInternal() {
		Config config = agent.getConfig(DEFAULT_CONFIG_NAME, Config.class);
		log.info(format("[组件:%s] %s", getName(), config.toString()));

		kafka = new KafkaProducer<>(config.connectProperties());
		
		instanceAgentCPUDataQueue = new LinkedBlockingQueue<>();
		instanceAgentMemoryDataQueue = new LinkedBlockingQueue<>();
		instanceAgentNetworkDataQueue = new LinkedBlockingQueue<>();
		instanceAgentDiskDataQueue = new LinkedBlockingQueue<>();
		
		executor = new ScheduledThreadPoolExecutor(6, new DiscardOldestPolicyWithLogger(getClass()));
	}

	/**
	 * 启动流程为开启5个传输线程，并根据配置项定时执行
	 */
	@Override
	protected void startInternal() {
		redisService = agent.getService(RedisService.DEFAULT_SERVICE_NAME, RedisService.class);
		Config c = agent.getConfig(DEFAULT_CONFIG_NAME, Config.class);

		int rate = c.sendRate();

		if(rate <= 0) {
			log.error("Send rate must be greater than 0");
			System.exit(1);
		}

		MonitorService.Config cfg = agent.getConfig(MonitorService.DEFAULT_CONFIG_NAME, MonitorService.Config.class);

		executor.scheduleAtFixedRate(() -> {
			log.info("Start process monitor data to kafka.");
			try {
				pushInstanceMointorDataToKafka(INS_CPU_DATA_QUEUE, StandardMonitorService.PREFIX_CPU);
				pushInstanceMointorDataToKafka(INS_RAM_DATA_QUEUE, StandardMonitorService.PREFIX_RAM);
				pushInstanceMointorDataToKafka(INS_NETIO_DATA_QUEUE, StandardMonitorService.PREFIX_NETWORK_IO);
				pushInstanceMointorDataToKafka(INS_DISKIO_DATA_QUEUE, StandardMonitorService.PREFIX_DISK_IO);
				pushInstanceMointorDataToKafka(INS_DISKCAP_DATA_QUEUE, StandardMonitorService.PREFIX_DISK_CAP);

				pushNovaMointorDataToKafka(NOVA_CPU_DATA_QUEUE, StandardMonitorService.NOVA_CPU);
				pushNovaMointorDataToKafka(NOVA_RAM_DATA_QUEUE, StandardMonitorService.NOVA_RAM);
				pushNovaMointorDataToKafka(NOVA_NETIO_DATA_QUEUE, StandardMonitorService.NOVA_NETWORK_IO);
				pushNovaMointorDataToKafka(NOVA_DISKIO_DATA_QUEUE, StandardMonitorService.NOVA_DISK_IO);
				pushNovaMointorDataToKafka(NOVA_DISKCAP_DATA_QUEUE, StandardMonitorService.NOVA_DISK_CAP);

				if (cfg.insAgentRecivePort() != -1) {
					pushInstanceAgentDataToKafka(instanceAgentCPUDataQueue, INSAGENT_CPU_DATA_QUEUE);
					pushInstanceAgentDataToKafka(instanceAgentMemoryDataQueue, INSAGENT_RAM_DATA_QUEUE);
					pushInstanceAgentDataToKafka(instanceAgentNetworkDataQueue, INSAGENT_NETWORK_DATA_QUEUE);
					pushInstanceAgentDataToKafka(instanceAgentDiskDataQueue, INSAGENT_DISK_DATA_QUEUE);
				}
			} catch (Exception e) {
				log.warn("Send data to kafka thread occur a exception", e);
			}

		}, 5, rate, TimeUnit.SECONDS);
	}
	
	@Override
	protected void stopInternal() {
		executor.shutdown();
		kafka.close();
	}
	
	/**
	 * 将存储在Redis中的云实例监控数据推送至RabbitMQ消息队列
	 * @param topic Kafka topic
	 * @param keyPrefix Redis键名前缀
	 */
	private void pushInstanceMointorDataToKafka(String topic, String keyPrefix) {
		List<String> vmIds = agent.getService(DomainManagerService.DEFAULT_SERVICE_NAME,
				DomainManagerService.class).getAllInstanceUUID();
		
		String key;
		List<List<String>> dataList = new ArrayList<>(vmIds.size());
		int len = 0;

		for(String uuid : vmIds) {
			key = keyPrefix + uuid;
			List<String> dl = redisService.getList(key);
			dataList.add(dl);
			len += dl.size() * dl.get(0).length();
			//redisService.delete(key);
		}
			
		String data = combineInstanceJSONMointorData(dataList, len);

		ProducerRecord<String, String> record = new ProducerRecord<>(topic, data);
		kafka.send(record, (meta, e) -> {
            if(e != null) {
                log.warn(format("[组件:%s] 向Kafka添加消息时发生连接异常", getName()), e);
            } else {
                for(String uuid : vmIds) {
					redisService.delete(keyPrefix + uuid);
				}
            }
        });
	}
	
	
	private static String combineInstanceJSONMointorData(List<List<String>> dataList, int initLength) {
		StringBuilder sb = new StringBuilder(initLength + 100);
		sb.append('[');
		for(List<String> dl : dataList) {
			for(String str : dl) {
				sb.append(str);
				sb.append(',');
			}
		}
		sb.append(']');
		return sb.toString();
	}
	
	
	/**
	 * 将存储在Redis缓存中的计算节点监控数据推送至RabbitMQ消息队列
	 * @param topic Kafka topic
	 * @param key Redis键名
	 */
	private void pushNovaMointorDataToKafka(String topic, String key) {
		List<String> dataList = redisService.getList(key);
		String data = combineNovaJSONMonitorData(dataList, dataList.listIterator());

		ProducerRecord<String, String> record = new ProducerRecord<>(topic, data);
		kafka.send(record, (meta, e) -> {
			if(e != null) {
				log.warn(format("[组件:%s] 向Kafka添加消息时发生连接异常", getName()), e);
			} else {
			    //确保成功发送后再从Redis中删除
			    redisService.delete(key);
            }
		});
	}

	
	private static String combineNovaJSONMonitorData(List<String> dataList, ListIterator<String> it) {
		StringBuilder sb = new StringBuilder(dataList.get(0).length() * dataList.size() + 100);
		sb.append('[');
		while(it.hasNext()) {
			sb.append(it.next());
			sb.append(',');
		}
		sb.append(']');
		return sb.toString();
	}
	
	
	/**
	 * 将虚拟机Agent模块采集的监控数据上报至RabbitMQ
	 * @param queue 持有监控数据的阻塞队列
	 */
	private void pushInstanceAgentDataToKafka(BlockingQueue<String> queue, String topic) {
		List<String> jsonList = new ArrayList<>(256);
		while(jsonList.size() <= 256) {
			String json;
			try {
				json = queue.poll(1, TimeUnit.MILLISECONDS);
				if(json != null)
					jsonList.add(json);
				else
					break;
			} catch (InterruptedException e) {
				break;
			}
		}

		String json = combineNovaJSONMonitorData(jsonList, jsonList.listIterator());
		ProducerRecord<String, String> record = new ProducerRecord<>(topic, json);
		kafka.send(record, (meta, e) -> {
			if(e != null) {
				log.warn(format("[组件:%s] 向Kafka添加消息时发生连接异常", getName()), e);
			}
		});
	}

	@Override
	public void transferInstanceAgentData(InstanceAgentDataType type, String json) {
		if(json == null || json.equals(""))
			return;
		try {
			switch (type) {
			case CPU:
				instanceAgentCPUDataQueue.put(json);
				break;
			case MEMORY:
				instanceAgentMemoryDataQueue.put(json);
				break;
			case DISK:
				instanceAgentDiskDataQueue.put(json);
				break;
			case NETWORK:
				instanceAgentNetworkDataQueue.put(json);
				break;
			}
		} catch (InterruptedException e) {
			log.warn("线程中断异常", e);
		}
	}
}
