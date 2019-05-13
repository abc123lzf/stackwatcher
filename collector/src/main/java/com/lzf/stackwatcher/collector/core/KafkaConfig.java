package com.lzf.stackwatcher.collector.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.collector.consumer.*;
import com.lzf.stackwatcher.common.AbstractConfig;
import com.lzf.stackwatcher.common.ConfigInitializationException;
import com.lzf.stackwatcher.common.ConfigManager;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class KafkaConfig extends AbstractConfig {

    static final String NAME = "config.kafka";

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

        try(InputStream is = configManager.loadResource(CONFIG_PATH)) {
            Properties p = new Properties();
            p.load(is);

            group = p.getProperty("kafka.group");

            topicMap.put(InstanceCPUConsumer.class, p.getProperty("kafka.topic.instance-cpu"));
            topicMap.put(InstanceNetworkIOConsumer.class, p.getProperty("kafka.topic.instance-network-io"));
            topicMap.put(InstanceDiskIOConsumer.class, p.getProperty("kafka.topic.instance-disk-io"));
            topicMap.put(InstanceDiskCapacityConsumer.class, p.getProperty("kafka.topic.instance-disk-capacity"));

            topicMap.put(NovaCPUConsumer.class, p.getProperty("kafka.topic.nova-cpu"));
            topicMap.put(NovaMemoryConsumer.class, p.getProperty("kafka.topic.nova-memory"));
            topicMap.put(NovaNetworkIOConsumer.class, p.getProperty("kafka.topic.nova-network-io"));
            topicMap.put(NovaDiskIOConsumer.class, p.getProperty("kafka.topic.nova-disk-io"));
            topicMap.put(NovaDiskCapacityConsumer.class, p.getProperty("kafka.topic.nova-disk-capacity"));

            topicMap.put(InstanceAgentCPUConsumer.class, p.getProperty("kafka.topic.instance-agent-cpu"));
            topicMap.put(InstanceAgentMemoryConsumer.class, p.getProperty("kafka.topic.instance-agent-memory"));
            topicMap.put(InstanceAgentNetworkIOConsumer.class, p.getProperty("kafka.topic.instance-agent-network"));
            topicMap.put(InstanceAgentDiskConsumer.class, p.getProperty("kafka.topic.instance-agent-disk"));

        } catch (Exception e) {
            throw new ConfigInitializationException(e);
        }
    }

    public Properties connectProperties() {
        Properties p = new Properties();
        StringBuilder sb = new StringBuilder();
        int i = 0, s = kafkaAddresses.size();
        for(String str : kafkaAddresses) {
            sb.append(str);
            if(i < s - 1)
                sb.append(',');
            i++;
        }

        p.put("bootstrap.servers", sb.toString());
        p.put("group.id", group);
        p.put("retries", "10");
        p.put("enable.auto.commit", "false");
        p.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        p.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return p;
    }

    public String getTopic(Class<? extends Consumer> klass) {
        return topicMap.get(klass);
    }
}
