package com.lzf.stackwatcher.collector.core;

import com.lzf.stackwatcher.zookeeper.ZooKeeper;
import com.lzf.stackwatcher.common.AbstractConfig;
import com.lzf.stackwatcher.common.ConfigInitializationException;
import com.lzf.stackwatcher.common.ConfigManager;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

public class ZooKeeperConfig extends AbstractConfig implements ZooKeeper.Config {

    public static final String NAME = "config.zookeeper";
    private static final String CONFIG_PATH = "classpath://config.properties";

    private String addresses;

    private int connectTimeout;

    private int sessionTimeout;

    private boolean remote;

    private String configPath;

    public ZooKeeperConfig(ConfigManager manager) {
        super(manager, NAME);
    }

    @Override
    protected void initInternal() throws ConfigInitializationException {
        try(InputStream is = configManager.loadResource(CONFIG_PATH)) {
            Properties p = new Properties();
            p.load(is);

            addresses = p.getProperty("zookeeper.address");
            connectTimeout = Integer.valueOf(p.getProperty("zookeeper.connection.timeout"));
            sessionTimeout = Integer.valueOf(p.getProperty("zookeeper.session.timeout"));
            remote = Boolean.valueOf(p.getProperty("config.remote"));
            if(remote) {
                String host = InetAddress.getLocalHost().getCanonicalHostName();
                configPath = "/stackwatcher/collector/config/" + host;
            }

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

    public boolean isRemote() {
        return remote;
    }

    public String getConfigPath() {
        return configPath;
    }
}
