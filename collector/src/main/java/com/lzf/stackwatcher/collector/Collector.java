package com.lzf.stackwatcher.collector;

import com.lzf.stackwatcher.collector.core.ConsumerManager;
import com.lzf.stackwatcher.collector.core.SubscriberManager;
import com.lzf.stackwatcher.collector.core.ZooKeeperConfig;
import com.lzf.stackwatcher.common.*;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;
import com.lzf.stackwatcher.zookeeper.ZooKeeperConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Collector extends ContainerBase<Void> implements ConfigManager {
    private static final Logger log = LoggerFactory.getLogger(Collector.class);

    private final InetAddress localAddress;

    private final ConfigManager configManager = new DefaultConfigManager("ConfigManager");

    // ZooKeeper连接对象
    private final ZooKeeper zooKeeper;

    // ZooKeeper主节点
    private final String zooPath;

    private final ConsumerManager consumerManager;

    private final SubscriberManager subscriberManager;

    public Collector() {
        try {
            this.localAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new Error(e);
        }

        registerConfig(new ZooKeeperConfig(configManager));
        this.zooKeeper = new ZooKeeperConnector(configManager);
        setName(String.format("Collector [%s]", localAddress.getHostAddress()));

        this.zooPath = "/stackwatcher/collector/" + localAddress.getHostName();

        consumerManager = new ConsumerManager(this);
        subscriberManager = new SubscriberManager(this);
    }

    @Override
    protected void initInternal() {
        try {
            zooKeeper.createTemporaryNodeRecursive(zooPath, new byte[0]);
        } catch (Exception e) {
            log.error("create znode " + zooPath + "failure.", e);
            System.exit(1);
        }

        consumerManager.init();
        subscriberManager.init();
    }

    @Override
    protected void startInternal() {
        consumerManager.start();
        consumerManager.start();
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
