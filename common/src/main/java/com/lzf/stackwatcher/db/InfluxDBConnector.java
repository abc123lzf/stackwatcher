package com.lzf.stackwatcher.db;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.influxdb.dto.Point.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;


public class InfluxDBConnector implements InfluxDatabase {
    private static final Logger log = LoggerFactory.getLogger(InfluxDBConnector.class);
    // 用户名
    private String username;
    // 密码
    private String password;
    // 连接地址
    private String openURL;
    // 保留策略
    private String retentionPolicy;

    private InfluxDB influxDB;

    public InfluxDBConnector(String username, String password, String openURL, String retentionPolicy) {
        log.info(String.format("InfluxDB配置: URL:%s User:%s RetentionPolicy:%s", openURL, username,
                retentionPolicy));
        this.username = username;
        this.password = password;
        this.openURL = openURL;
        this.retentionPolicy = retentionPolicy;
        this.influxDB = InfluxDBFactory.connect(openURL, username, password);
        influxDB.setRetentionPolicy(retentionPolicy);
        influxDB.setLogLevel(InfluxDB.LogLevel.NONE);
    }

    @Override
    public String getDefaultRetentionPolicy() {
        return retentionPolicy;
    }

    @Override
    public void createDatabase(String dbName) {
        influxDB.query(new Query("create database " + dbName, dbName));
    }

    @Override
    public void deleteDatabase(String dbName) {
        influxDB.query(new Query("drop database " + dbName, dbName));
    }


    @Override
    public boolean ping() {
        boolean isConnected = false;
        Pong pong;
        try {
            pong = influxDB.ping();
            if (pong != null) {
                isConnected = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnected;
    }

    @Override
    public void createRetentionPolicy(String dbName, String policyName,
                                      int duration, int replication,
                                      boolean isDefault) {
        String sql = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s ", policyName,
                dbName, duration + "h", replication);
        if (isDefault) {
            sql += " DEFAULT";
        }
        this.query(sql, dbName);
    }


    @Override
    public QueryResult query(String command, String database) {
        return influxDB.query(new Query(command, database));
    }

    /**
     * 插入
     *
     * @param measurement
     *            表
     * @param tags
     *            标签
     * @param fields
     *            字段
     */
    public void insert(String dbName, String measurement, Map<String, String> tags,
                       Map<String, Object> fields, long time, TimeUnit timeUnit) {
        Builder builder = Point.measurement(measurement);
        builder.tag(tags);
        builder.fields(fields);
        if (time != 0L) {
            builder.time(time, timeUnit);
        }
        influxDB.write(dbName, retentionPolicy, builder.build());
    }

    /**
     * 批量写入测点
     * @param batchPoints
     */
    public void insertBatch(BatchPoints batchPoints) {
        influxDB.write(batchPoints);
    }

    /**
     * 关闭数据库连接
     */
    @Override
    public void close() {
        influxDB.close();
    }


    @Override
    protected void finalize() {
        try {
            if(influxDB != null)
                close();
        } catch (Throwable ignore) {
            // NOOP
        }
    }
}