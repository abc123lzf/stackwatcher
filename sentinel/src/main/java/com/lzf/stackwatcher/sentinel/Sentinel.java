package com.lzf.stackwatcher.sentinel;

import com.lzf.stackwatcher.common.*;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;
import com.lzf.stackwatcher.zookeeper.ZooKeeperConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class Sentinel extends ContainerBase<Void> implements ConfigManager {

    private ZooKeeper zooKeeper;

    private final ConfigManager configManager = new DefaultConfigManager("ConfigManager");

    public Sentinel() {
        setName("Sentinel");
        init();
        start();
    }

    @Override
    protected void initInternal() {
        ZooKeeperConfig cfg = new ZooKeeperConfig(this);
        registerConfig(cfg);
    }

    @Override
    protected void startInternal() {
        zooKeeper = new ZooKeeperConnector(this);
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
