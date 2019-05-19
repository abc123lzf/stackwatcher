package com.lzf.stackwatcher.collector;

import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.collector.core.ConsumerManager;
import com.lzf.stackwatcher.collector.core.KafkaConfig;
import com.lzf.stackwatcher.collector.core.SubscriberManager;
import com.lzf.stackwatcher.collector.core.ZooKeeperConfig;
import com.lzf.stackwatcher.collector.subscriber.InfluxDBConfig;
import com.lzf.stackwatcher.common.*;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;
import com.lzf.stackwatcher.zookeeper.ZooKeeperConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

public class Collector extends ContainerBase<Void> implements ConfigManager {
    private static final Logger log = LoggerFactory.getLogger(Collector.class);

    private final InetAddress localAddress;

    private final ConfigManager configManager = new DefaultConfigManager("ConfigManager");

    // ZooKeeper主节点
    private String zooPath;

    // ZooKeeper连接对象
    private ZooKeeper zooKeeper;

    private ConsumerManager consumerManager;

    private SubscriberManager subscriberManager;

    public Collector() {
        try {
            this.localAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new Error(e);
        }
    }

    @Override
    protected void initInternal() {
        ZooKeeperConfig cfg = new ZooKeeperConfig(configManager);
        registerConfig(cfg);

        this.zooKeeper = new ZooKeeperConnector(configManager);

        setName(String.format("Collector [%s]", localAddress.getHostAddress()));

        this.zooPath = "/stackwatcher/collector/" + localAddress.getCanonicalHostName();

        consumerManager = new ConsumerManager(this);
        subscriberManager = new SubscriberManager(this);

        consumerManager.init();
        subscriberManager.init();

        if(cfg.isRemote()) {
            try {
                zooKeeper.registerWatcher(cfg.getConfigPath(), e -> {
                    log.info("Detected config node {} changed, ready to restart", cfg.getConfigPath());
                    restart();
                    log.info("Restart complete");
                });
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    protected void startInternal() {
        consumerManager.start();
        subscriberManager.start();

        InfluxDBConfig dbcfg = getConfig(InfluxDBConfig.NAME, InfluxDBConfig.class);
        KafkaConfig kc = getConfig(KafkaConfig.NAME, KafkaConfig.class);
        JSONObject obj = new JSONObject();
        obj.put("host", localAddress.getCanonicalHostName());
        obj.put("persistence", dbcfg);
        obj.put("persistence-db", "influxDB");
        obj.put("kafka-group-id", kc.getGroup());
        obj.put("using-kafka-addresses", kc.getKafkaAddresses());

        try {
            zooKeeper.createTemporaryNodeRecursive(zooPath, obj.toJSONString().getBytes(Charset.forName("UTF-8")));
        } catch (Exception e) {
            log.error("create znode " + zooPath + "failure.", e);
            System.exit(1);
        }
    }

    @Override
    protected void stopInternal() {
        zooKeeper.close();
        consumerManager.stop();
        subscriberManager.stop();
    }

    @Override
    protected void restartInternal() {
        stop();
        init();
        start();
    }

    public InetAddress getLocalAddress() {
        return localAddress;
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public ConsumerManager getConsumerManager() {
        return consumerManager;
    }

    public SubscriberManager getSubscriberManager() {
        return subscriberManager;
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
        configManager.registerConfig(config);
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

    @Override
    public InputStream loadResource(String path) throws Exception {
        return configManager.loadResource(path);
    }
}
