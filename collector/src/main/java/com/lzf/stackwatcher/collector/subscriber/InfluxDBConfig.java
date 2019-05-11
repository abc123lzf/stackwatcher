package com.lzf.stackwatcher.collector.subscriber;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.common.AbstractConfig;
import com.lzf.stackwatcher.common.ConfigInitializationException;
import com.lzf.stackwatcher.common.ConfigManager;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.*;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Influx DB 数据库配置
 */
public class InfluxDBConfig extends AbstractConfig {

    static final String NAME = "config.influxdb";

    private static final String CONFIG_PATH = "classpath://influxdb.properties";

    private static final String INFLUXDB_ZOOKEEPER_PATH = "/stackwatcher/influxdb";

    private final ZooKeeper zooKeeper;

    //数据库服务器主机名
    private String host;
    //端口
    private int port;
    //用户名
    private String username;
    //密码
    private String password;
    //数据库名称
    private String dbName;
    //采用的保留策略
    private String retentionPolicy;
    //数据类型和表名映射
    private final Map<Class<? extends TimeSeriesData>, String> tableMap = new ConcurrentHashMap<>(32);

    public InfluxDBConfig(ConfigManager configManager, ZooKeeper zooKeeper) {
        super(configManager, NAME);
        this.zooKeeper = zooKeeper;
    }

    @Override
    protected void initInternal() throws ConfigInitializationException {
        try {
            List<String> influxList = zooKeeper.getChildNode(INFLUXDB_ZOOKEEPER_PATH);
            if(influxList == null || influxList.size() == 0) {
                throw new ConfigInitializationException("No influxDB defined in zookeeper");
            }

            Random r = new Random();
            String json = new String(zooKeeper.readNode(influxList.get(r.nextInt(influxList.size()))), Charset.forName("UTF-8"));

            JSONObject obj = JSON.parseObject(json);
            this.host = obj.getString("host");
            this.port = obj.getIntValue("port");
            this.username = obj.getString("username");
            this.password = obj.getString("password");

        } catch (Exception e) {
            throw new ConfigInitializationException(e);
        }


        try(InputStream is = configManager.loadResource(CONFIG_PATH)) {
            Properties p = new Properties();
            p.load(is);

            dbName = p.getProperty("influxdb.db-name");
            retentionPolicy = p.getProperty("influxdb.retention-policy");

            tableMap.put(InstanceCPUMonitorData.class, p.getProperty("influxdb.table.instance-cpu"));
            tableMap.put(InstanceMemoryMonitorData.class, p.getProperty("influxdb.table.instance-memory"));
            tableMap.put(InstanceNetworkIOMonitorData.class, p.getProperty("influxdb.table.instance-network-io"));
            tableMap.put(InstanceDiskIOMonitorData.class, p.getProperty("influxdb.table.instance-disk-io"));
            tableMap.put(InstanceDiskCapacityMonitorData.class, p.getProperty("influxdb.table.instance-disk-cap"));

            tableMap.put(NovaCPUMonitorData.class, p.getProperty("influxdb.table.nova-cpu"));
            tableMap.put(NovaMemoryMonitorData.class, p.getProperty("influxdb.table.nova-memory"));
            tableMap.put(NovaNetworkIOMonitorData.class, p.getProperty("influxdb.table.nova-network-io"));
            tableMap.put(NovaDiskIOMonitorData.class, p.getProperty("influxdb.table.nova-disk-io"));
            tableMap.put(NovaDiskCapacityMonitorData.class, p.getProperty("influxdb.table.nova-disk-cap"));

            tableMap.put(InstanceAgentCPUMonitorData.class, p.getProperty("influxdb.table.instance-agent-cpu"));
            tableMap.put(InstanceAgentMemoryMonitorData.class, p.getProperty("influxdb.table.instance-agent-memory"));
            tableMap.put(InstanceAgentNetworkMonitorData.class, p.getProperty("influxdb.table.instance-agent-network-io"));
            tableMap.put(InstanceAgentDiskMonitorData.class, p.getProperty("influxdb.table.instance-agent-disk"));

        } catch (Exception e) {
            throw new ConfigInitializationException(e);
        }
    }

    public String getDbHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDbName() {
        return dbName;
    }

    public String getRetentionPolicy() {
        return retentionPolicy;
    }

    public String getTableName(Class<? extends TimeSeriesData> dataClass) {
        return tableMap.get(dataClass);
    }
}
