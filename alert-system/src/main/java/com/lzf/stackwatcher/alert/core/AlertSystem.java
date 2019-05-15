package com.lzf.stackwatcher.alert.core;

import com.lzf.stackwatcher.alert.configuration.KafkaConfig;
import com.lzf.stackwatcher.alert.configuration.ZooKeeperConfig;
import com.lzf.stackwatcher.common.ConfigManager;
import com.lzf.stackwatcher.common.ContainerBase;
import com.lzf.stackwatcher.common.DefaultConfigManager;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;
import com.lzf.stackwatcher.zookeeper.ZooKeeperConnector;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AlertSystem extends ContainerBase<Void> {

    private final ConfigManager configManager = new DefaultConfigManager("ConfigManager");

    private ZooKeeper zooKeeper;

    private ExecutorService executor;

    public AlertSystem() {
        setName("AlertSystem");
        init();
        start();
    }

    @Override
    protected void initInternal() {
        executor = new ThreadPoolExecutor(14, 24, 500, TimeUnit.SECONDS, new SynchronousQueue<>());
        configManager.registerConfig(new ZooKeeperConfig(configManager));
    }

    @Override
    protected void startInternal() {
        zooKeeper = new ZooKeeperConnector(configManager);
        configManager.registerConfig(new KafkaConfig(configManager, zooKeeper));
    }

    @Bean
    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Bean
    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }
}
