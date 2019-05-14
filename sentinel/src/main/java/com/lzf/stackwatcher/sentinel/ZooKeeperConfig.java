package com.lzf.stackwatcher.sentinel;

import com.lzf.stackwatcher.common.AbstractConfig;
import com.lzf.stackwatcher.common.ConfigInitializationException;
import com.lzf.stackwatcher.common.ConfigManager;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;

import java.io.InputStream;
import java.util.Properties;

public class ZooKeeperConfig extends AbstractConfig implements ZooKeeper.Config {
    static final String NAME = ZooKeeper.DEFAULT_CONFIG_NAME;
    static final String CONFIG_PATH = "classpath://config.properties";

    private String addresses;

    ZooKeeperConfig(ConfigManager configManager) {
        super(configManager, NAME);
    }

    @Override
    protected void initInternal() throws ConfigInitializationException {
        try(InputStream is = configManager.loadResource(CONFIG_PATH)) {
            Properties p = new Properties();
            p.load(is);

            addresses = p.getProperty("zookeeper.host");

        } catch (Exception e) {
            throw new ConfigInitializationException(e);
        }
    }

    public String getAddresses() {
        return addresses;
    }

    @Override
    public int getConnectTimeout() {
        return 0;
    }

    @Override
    public int getSessionTimeout() {
        return 0;
    }
}
