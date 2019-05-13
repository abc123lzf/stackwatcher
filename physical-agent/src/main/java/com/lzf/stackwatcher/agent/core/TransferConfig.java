package com.lzf.stackwatcher.agent.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.agent.TransferService;
import com.lzf.stackwatcher.common.AbstractConfig;
import com.lzf.stackwatcher.common.ConfigInitializationException;
import com.lzf.stackwatcher.common.ConfigManager;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

public class TransferConfig extends AbstractConfig implements TransferService.Config {

    private static final String CONFIG_PATH = "classpath://config.properties";
    private static final String ZOOKEEPER_KAFKA_PATH = "/brokers/ids";

    private final List<String> addresses = new CopyOnWriteArrayList<>();

    private int sendRate;

    private final ZooKeeper zooKeeper;

    TransferConfig(ConfigManager configManager, ZooKeeper zooKeeper) {
        super(configManager, TransferService.DEFAULT_CONFIG_NAME);
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

            addresses.addAll(out);
        } catch (Exception e) {
            throw new ConfigInitializationException(e);
        }

        try(InputStream is = configManager.loadResource(CONFIG_PATH)) {
            Properties p = new Properties();
            p.load(is);

            sendRate = Integer.valueOf(p.getProperty("monitor.sendrate"));

        } catch (Exception e) {
            throw new ConfigInitializationException(e);
        }
    }

    @Override
    public String kafkaAddresses() {
        StringBuilder sb = new StringBuilder();
        int i = 0, s = addresses.size();
        for(String str : addresses) {
            sb.append(str);
            if(i < s - 1)
                sb.append(',');
            i++;
        }

        return sb.toString();
    }

    @Override
    public int sendRate() {
        return sendRate;
    }

    @Override
    public Properties connectProperties() {
        Properties p = new Properties();
        p.setProperty("bootstrap.servers", kafkaAddresses());
        p.setProperty("max.request.size", "10485760");
        p.setProperty("acks", "all");
        p.setProperty("retries", "0");
        p.setProperty("batch.size", "16384");
        p.setProperty("linger.ms", "1");
        p.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        p.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return p;
    }

    @Override
    public String toString() {
        return "TransferConfig{" +
                "address='" + kafkaAddresses() + '\'' +
                ", sendRate=" + sendRate +
                '}';
    }
}
