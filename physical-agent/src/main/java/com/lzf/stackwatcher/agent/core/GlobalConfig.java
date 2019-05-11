package com.lzf.stackwatcher.agent.core;

import com.lzf.stackwatcher.common.AbstractConfig;
import com.lzf.stackwatcher.common.ConfigInitializationException;
import com.lzf.stackwatcher.common.ConfigManager;

import java.io.InputStream;
import java.util.Properties;

public class GlobalConfig extends AbstractConfig {

    private static final String CONFIG_PATH = "classpath://config.properties";

    static final String NAME = "config.global";

    private String zookeeperAddresses;

    private String libvirtAddress;


    GlobalConfig(ConfigManager configManager) {
        super(configManager, NAME);
    }

    @Override
    protected void initInternal() throws ConfigInitializationException {
        try(InputStream is = configManager.loadResource(CONFIG_PATH)) {
            Properties p = new Properties();
            p.load(is);

            zookeeperAddresses = p.getProperty("zookeeper.host");
            libvirtAddress = p.getProperty("libvirt.url");

        } catch (Exception e) {
            throw new ConfigInitializationException(e);
        }
    }

    public String zookeeperAddresses() {
        return zookeeperAddresses;
    }

    public String libvirtAddress() {
        return libvirtAddress;
    }
}
