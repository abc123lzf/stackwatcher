package com.lzf.stackwatcher.agent.core;

import com.lzf.stackwatcher.agent.RedisService;
import com.lzf.stackwatcher.common.AbstractConfig;
import com.lzf.stackwatcher.common.ConfigInitializationException;
import com.lzf.stackwatcher.common.ConfigManager;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Properties;

public class RedisConfig extends AbstractConfig implements RedisService.Config {

    private static final String CONFIG_PATH = "classpath://config.properties";

    private final ZooKeeper zooKeeper;

    private String host = "localhost";
    private int port = 6579;
    private String pass = null;
    private int dbNum = 0;

    private int maxTotalConnect = 5;
    private int maxIdleConnect = 5;
    private int minIdleConnect = 5;

    RedisConfig(ConfigManager configManager, ZooKeeper zooKeeper) {
        super(configManager, RedisService.DEFAULT_CONFIG_NAME);
        this.zooKeeper = zooKeeper;
    }

    @Override
    protected void initInternal() throws ConfigInitializationException {
        GlobalConfig cfg = configManager.getConfig(GlobalConfig.NAME, GlobalConfig.class);
        if(!cfg.isRemoteConfig()) {
            try (InputStream is = configManager.loadResource(CONFIG_PATH)) {
                Properties p = new Properties();
                p.load(is);
                readConfig(p);
            } catch (Exception e) {
                throw new ConfigInitializationException(e);
            }
        } else {
            try {
                byte[] b = zooKeeper.readNode(cfg.getZNodePath());
                InputStream bis = new ByteArrayInputStream(b);
                Properties p = new Properties();
                p.load(bis);
                readConfig(p);
            } catch (UnknownHostException e) {
                throw new Error(e);
            } catch (Exception e) {
                throw new ConfigInitializationException(e);
            }
        }
    }

    private void readConfig(final Properties p) throws NumberFormatException {
        host = p.getProperty("redis.host");
        port = Integer.valueOf(p.getProperty("redis.port"));
        String s;
        pass = (s = p.getProperty("redis.pass")) == null || s.equals("") ? null : s;
        dbNum = Integer.valueOf(p.getProperty("redis.db"));
        maxTotalConnect = Integer.valueOf(p.getProperty("redis.pool.max-total-connect"));
        maxIdleConnect = Integer.valueOf(p.getProperty("redis.pool.max-idle-connect"));
        minIdleConnect = Integer.valueOf(p.getProperty("redis.pool.min-idle-connect"));
    }

    @Override
    public String host() {
        return host;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public String pass() {
        return pass;
    }

    @Override
    public int databaseId() {
        return dbNum;
    }

    @Override
    public int maxTotalConnect() {
        return maxTotalConnect;
    }

    @Override
    public int maxIdleConnect() {
        return maxIdleConnect;
    }

    @Override
    public int minIdleConnect() {
        return minIdleConnect;
    }
}
