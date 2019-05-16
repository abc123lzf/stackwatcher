package com.lzf.stackwatcher.alert.core;

import com.lzf.stackwatcher.alert.configuration.AlertMessageConfig;
import com.lzf.stackwatcher.alert.configuration.KafkaConfig;
import com.lzf.stackwatcher.alert.configuration.ZooKeeperConfig;
import com.lzf.stackwatcher.common.*;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;
import com.lzf.stackwatcher.zookeeper.ZooKeeperConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class AlertSystem extends ContainerBase<Void> implements ConfigManager {

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
        configManager.registerConfig(new AlertMessageConfig(configManager));
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

    @Override
    public InputStream loadResource(String path) throws Exception {
        return configManager.loadResource(path);
    }
}
