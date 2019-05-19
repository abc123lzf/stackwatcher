package com.lzf.stackwatcher.collector.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.collector.consumer.*;
import com.lzf.stackwatcher.common.AbstractConfig;
import com.lzf.stackwatcher.common.ConfigInitializationException;
import com.lzf.stackwatcher.common.ConfigManager;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class KafkaConfig extends AbstractConfig {

    public static final String NAME = "config.kafka";

    private static final String CONFIG_PATH = "classpath://kafka.properties";

    private static final String ZOOKEEPER_KAFKA_PATH = "/brokers/ids";

    private String group;

    private final ZooKeeper zooKeeper;

    private final List<String> kafkaAddresses = new CopyOnWriteArrayList<>();

    private final Map<Class<? extends Consumer>, String> topicMap = new ConcurrentHashMap<>(32);

    public KafkaConfig(ConfigManager configManager, ZooKeeper zooKeeper) {
        super(configManager, NAME);
        this.zooKeeper = zooKeeper;
    }

    @Override
    protected void initInternal() throws ConfigInitializationException {
        try {
            List<String> list = zooKeeper.getChildNode(ZOOKEEPER_KAFKA_PATH);
            if(list == null)
                throw new ConfigInitializationException("No kafka server found in zookeeper server");
            List<String> out = new ArrayList<>();
            for(String path : list) {
                String json = new String(zooKeeper.readNode(ZOOKEEPER_KAFKA_PATH + "/" + path), Charset.forName("UTF-8"));
                JSONObject obj = JSON.parseObject(json);
                out.add(String.format("%s:%d", obj.getString("host"), obj.getIntValue("port")));
            }

            kafkaAddresses.addAll(out);
        } catch (Exception e) {
            throw new ConfigInitializationException(e);
        }

        ZooKeeperConfig cfg = configManager.getConfig(ZooKeeperConfig.NAME, ZooKeeperConfig.class);
        if(!cfg.isRemote()) {
            try (InputStream is = configManager.loadResource(CONFIG_PATH)) {
                Properties p = new Properties();
                p.load(is);
                readConfig(p);
            } catch (Exception e) {
                throw new ConfigInitializationException(e);
            }
        } else {
            try {
                byte[] b = zooKeeper.readNode(cfg.getConfigPath());
                InputStream bis = new ByteArrayInputStream(b);
                Properties p = new Properties();
                p.load(bis);
                readConfig(p);
            } catch (Exception e) {
                throw new ConfigInitializationException(e);
            }
        }
    }

    private void readConfig(Properties p) {
        group = p.getProperty("kafka.group");

        if(Boolean.valueOf(p.getProperty("kafka.instance-cpu.enable")))
            topicMap.put(InstanceCPUConsumer.class, p.getProperty("kafka.topic.instance-cpu"));
        if(Boolean.valueOf(p.getProperty("kafka.instance-network-io.enable")))
            topicMap.put(InstanceNetworkIOConsumer.class, p.getProperty("kafka.topic.instance-network-io"));
        if(Boolean.valueOf(p.getProperty("kafka.instance-disk-io.enable")))
            topicMap.put(InstanceDiskIOConsumer.class, p.getProperty("kafka.topic.instance-disk-io"));
        if(Boolean.valueOf(p.getProperty("kafka.instance-disk-capacity.enable")))
            topicMap.put(InstanceDiskCapacityConsumer.class, p.getProperty("kafka.topic.instance-disk-capacity"));

        if(Boolean.valueOf(p.getProperty("kafka.nova-cpu.enable")))
            topicMap.put(NovaCPUConsumer.class, p.getProperty("kafka.topic.nova-cpu"));
        if(Boolean.valueOf(p.getProperty("kafka.nova-memory.enable")))
            topicMap.put(NovaMemoryConsumer.class, p.getProperty("kafka.topic.nova-memory"));
        if(Boolean.valueOf(p.getProperty("kafka.nova-network.enable")))
            topicMap.put(NovaNetworkIOConsumer.class, p.getProperty("kafka.topic.nova-network-io"));
        if(Boolean.valueOf(p.getProperty("kafka.nova-disk-io.enable")))
            topicMap.put(NovaDiskIOConsumer.class, p.getProperty("kafka.topic.nova-disk-io"));
        if(Boolean.valueOf(p.getProperty("kafka.nova-disk-capacity.enable")))
            topicMap.put(NovaDiskCapacityConsumer.class, p.getProperty("kafka.topic.nova-disk-capacity"));

        if(Boolean.valueOf(p.getProperty("kafka.instance-agent-cpu.enable")))
            topicMap.put(InstanceAgentCPUConsumer.class, p.getProperty("kafka.topic.instance-agent-cpu"));
        if(Boolean.valueOf(p.getProperty("kafka.instance-agent-memory.enable")))
            topicMap.put(InstanceAgentMemoryConsumer.class, p.getProperty("kafka.topic.instance-agent-memory"));
        if(Boolean.valueOf(p.getProperty("kafka.instance-agent-network.enable")))
            topicMap.put(InstanceAgentNetworkIOConsumer.class, p.getProperty("kafka.topic.instance-agent-network"));
        if(Boolean.valueOf(p.getProperty("kafka.instance-agent-disk.enable")))
            topicMap.put(InstanceAgentDiskConsumer.class, p.getProperty("kafka.topic.instance-agent-disk"));

        if(Boolean.valueOf(p.getProperty("kafka.storage-pool.enable")))
            topicMap.put(StoragePoolConsumer.class, p.getProperty("kafka.topic.nova-storage-pool"));
        if(Boolean.valueOf(p.getProperty("kafka.storage-vol.enable")))
            topicMap.put(StorageVolConsumer.class, p.getProperty("kafka.topic.nova-storage-vol"));
    }


    public Properties connectProperties() {
        Properties p = new Properties();
        p.put("bootstrap.servers", getKafkaAddresses());
        p.put("group.id", group);
        p.put("retries", "10");
        p.put("enable.auto.commit", "false");
        p.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        p.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return p;
    }

    public String getKafkaAddresses() {
        StringBuilder sb = new StringBuilder();
        int i = 0, s = kafkaAddresses.size();
        for(String str : kafkaAddresses) {
            sb.append(str);
            if(i < s - 1)
                sb.append(',');
            i++;
        }
        return sb.toString();
    }

    public String getTopic(Class<? extends Consumer> klass) {
        return topicMap.get(klass);
    }

    public String getGroup() {
        return group;
    }
}
