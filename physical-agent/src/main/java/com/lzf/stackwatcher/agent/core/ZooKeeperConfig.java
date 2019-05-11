package com.lzf.stackwatcher.agent.core;

import com.lzf.stackwatcher.common.AbstractConfig;
import com.lzf.stackwatcher.common.ConfigInitializationException;
import com.lzf.stackwatcher.common.ConfigManager;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;

import java.io.InputStream;
import java.util.Properties;

public class ZooKeeperConfig extends AbstractConfig implements ZooKeeper.Config {

    public static final String NAME = "config.zookeeper";
    private static final String CONFIG_PATH = "classpath://config.properties";

    private String addresses;

    private int connectTimeout;

    private int sessionTimeout;

    public ZooKeeperConfig(ConfigManager manager) {
        super(manager, NAME);
    }

    @Override
    protected void initInternal() throws ConfigInitializationException {
        try(InputStream is = configManager.loadResource(CONFIG_PATH)) {
            Properties p = new Properties();
            p.load(is);

            addresses = p.getProperty("zookeeper.host");
            connectTimeout = Integer.valueOf(p.getProperty("zookeeper.connection.timeout"));
            sessionTimeout = Integer.valueOf(p.getProperty("zookeeper.session.timeout"));

        } catch (Exception e) {
            throw new ConfigInitializationException(e);
        }
    }

    @Override
    public String getAddresses() {
        return addresses;
    }

    @Override
    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public int getSessionTimeout() {
        return sessionTimeout;
    }
}
