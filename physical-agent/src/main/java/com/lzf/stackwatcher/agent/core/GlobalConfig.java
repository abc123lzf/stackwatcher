package com.lzf.stackwatcher.agent.core;

import com.lzf.stackwatcher.common.AbstractConfig;
import com.lzf.stackwatcher.common.ConfigInitializationException;
import com.lzf.stackwatcher.common.ConfigManager;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Objects;
import java.util.Properties;

public class GlobalConfig extends AbstractConfig {

    private static final String CONFIG_PATH = "classpath://config.properties";

    static final String NAME = "config.global";

    private final ZooKeeper zooKeeper;

    //Libvirt连接URL
    private String libvirtAddress;

    //是否是远程配置
    private boolean remote;

    private String znodePath;

    GlobalConfig(ConfigManager configManager, ZooKeeper zooKeeper) {
        super(configManager, NAME);
        this.zooKeeper = zooKeeper;
    }

    @Override
    protected void initInternal() throws ConfigInitializationException {
        try(InputStream is = configManager.loadResource(CONFIG_PATH)) {
            Properties p = new Properties();
            p.load(is);

            remote = p.getProperty("config.mode").equals("remote");
            if(remote) {
                String host = InetAddress.getLocalHost().getCanonicalHostName();
                byte[] b = zooKeeper.readNode(znodePath = "/stackwatcher/agent/config/" + host);
                InputStream bis = new ByteArrayInputStream(b);
                Properties rp = new Properties();
                rp.load(bis);
                libvirtAddress = Objects.requireNonNull(rp.getProperty("libvirt.url"),
                        "Remote config in ZooKeeper at " + host + "doesn't have \"libvirt.url\"");

            } else {
                libvirtAddress = Objects.requireNonNull(p.getProperty("libvirt.url"),
                        "Local config file \"config.properties\" doesn't have \"libvirt.url\"");
            }



        } catch (Exception e) {
            throw new ConfigInitializationException(e);
        }
    }


    public String libvirtAddress() {
        return libvirtAddress;
    }

    public boolean isRemoteConfig() {
        return remote;
    }

    public String getZNodePath() {
        return znodePath;
    }
}
