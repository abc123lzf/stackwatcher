package com.lzf.stackwatcher.alert.configuration;

import com.lzf.stackwatcher.common.AbstractConfig;
import com.lzf.stackwatcher.common.ConfigInitializationException;
import com.lzf.stackwatcher.common.ConfigManager;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;

import java.io.InputStream;
import java.util.Properties;

public class ZooKeeperConfig extends AbstractConfig implements ZooKeeper.Config {
    public static final String NAME = ZooKeeper.DEFAULT_CONFIG_NAME;
    private static final String CONFIG_PATH = "classpath://config.properties";

    private String nodeName;

    private String addresses;

    private int connectionTimeout;

    private int sessionTimeout;

    public ZooKeeperConfig(ConfigManager configManager) {
        super(configManager, NAME);
    }

    @Override
    protected void initInternal() throws ConfigInitializationException {
        try(InputStream is = configManager.loadResource(CONFIG_PATH)) {
            Properties p = new Properties();
            p.load(is);

            addresses = p.getProperty("zookeeper.host");
            connectionTimeout = Integer.valueOf(p.getProperty("zookeeper.connection.timeout"));
            sessionTimeout = Integer.valueOf(p.getProperty("zookeeper.session.timeout"));
            nodeName = p.getProperty("node.name");

        } catch (Exception e) {
            throw new ConfigInitializationException(e);
        }
    }

    public String getAddresses() {
        return addresses;
    }

    @Override
    public int getConnectTimeout() {
        return connectionTimeout;
    }

    @Override
    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public String getNodeName() {
        return nodeName;
    }
}
