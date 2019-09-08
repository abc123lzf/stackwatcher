package com.lzf.stackwatcher.db;

import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import com.lzf.stackwatcher.entity.TimeSeriesData;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * InfluxDB时间序列数据库操作接口
 */
public interface InfluxDatabase extends Closeable, AutoCloseable {

    /**
     * @return 默认的保留策略名
     */
    default String getDefaultRetentionPolicy() {
        return "autogen";
    }

    /**
     * 创建一个数据库
     * @param dbName 数据库名称
     */
    void createDatabase(String dbName);

    /**
     * 删除指定数据库
     * @param dbName 数据库名称
     */
    void deleteDatabase(String dbName);

    /**
     * 测试到数据库的连接是否正常
     * @return 连接是否正常
     */
    boolean ping();

    /**
     * 创建一个数据保留策略，相当于以下语句：
     * CREATE RETENTION POLICY <retention_policy_name> ON <database_name>
     *     DURATION <duration> REPLICATION <n> [SHARD DURATION <duration>]
     *     [DEFAULT]
     * @param database 目标数据库
     * @param policyName 保留策略名称
     * @param duration 数据保存小时数
     * @param replication 保存的副本数量
     * @param isDefault 是否设置为默认保留策略
     */
    void createRetentionPolicy(String database, String policyName,
                               int duration, int replication, boolean isDefault);

    /**
     * 为指定数据库创造一个默认的保留策略：策略名default，保存天数30天，副本1个
     * @param database 目标数据库
     */
    default void createDefaultRententionPolicy(String database) {
        createRetentionPolicy(database, "default", 30, 1, true);
    }

    /**
     * 向数据库发起一个查询语句
     * @param cmd 查询语句
     * @param dbName 目标数据库
     * @return 查询结果集
     */
    QueryResult query(String cmd, String dbName);

    /**
     * 向数据库插入一条数据
     * @param dbName 数据库名
     * @param measurement 表名
     * @param tags 索引字段映射集
     * @param fields 数据字段映射集
     * @param time 时间
     * @param timeUnit 时间单位
     */
    void insert(String dbName, String measurement, Map<String, String> tags,
                Map<String, Object> fields, long time, TimeUnit timeUnit);

    /**
     * 批量写入数据
     * @param batchPoints BatchPoints对象
     */
    void insertBatch(BatchPoints batchPoints);


    /**
     * 关闭当前数据库连接
     */
    void close();

    /**
     * 构造一个BatchPoints对象，用于批量写入数据
     * @param dbName 数据库名
     * @param tableName 表名
     * @param tags 索引字段
     * @param retentionPolicy 保留策略
     * @param level 一致性等级
     * @param dataList 待写入的数据集
     * @return 构造好的BatchPoints
     */
    static BatchPoints buildBatchPoint(String dbName,
                                       String tableName,
                                       @Nullable Map<String, String> tags,
                                       @Nullable String retentionPolicy,
                                       @Nullable InfluxDB.ConsistencyLevel level,
                                       @Nullable List<TimeSeriesData> dataList) {
        Objects.requireNonNull(dbName);
        BatchPoints.Builder bd = BatchPoints.database(dbName);
        if(tags != null) {
            for (Map.Entry<String, String> entry : tags.entrySet())
                bd.tag(entry.getKey(), entry.getValue());
        }

        if(retentionPolicy != null)
            bd.retentionPolicy(retentionPolicy);
        else
            bd.retentionPolicy("default");

        if(level != null)
            bd.consistency(level);
        else
            bd.consistency(InfluxDB.ConsistencyLevel.ALL);

        BatchPoints bp = bd.build();
        if(dataList != null && dataList.size() > 0) {
            for(TimeSeriesData data : dataList) {
                bp.point(buildPoint(tableName, data.getTags(),
                        data.getFields(), data.getTime(), TimeUnit.MILLISECONDS));
            }
        }

        return bp;
    }

    /**
     * 构造一个Point对象，相当于一条数据
     * @param measurement 表名
     * @param tags 索引字段映射集
     * @param fields 数据字段映射集
     * @param time 时间
     * @param timeUnit 时间单位
     */
    static Point buildPoint(String measurement, Map<String, String> tags,
                            Map<String, Object> fields, long time,
                            TimeUnit timeUnit) {
        if(measurement == null || timeUnit == null)
            throw new NullPointerException();
        Point.Builder p = Point.measurement(measurement);
        if(tags != null)
            p.tag(tags);
        if(fields != null)
            p.fields(fields);
        p.time(time, timeUnit);
        return p.build();
    }

    /**
     * 对实现了TimeSeriesDatabaseData接口的对象构造对应的Point
     * @param data TimeSeriesDatabaseData对象
     * @return Point对象
     */
    static Point buildPoint(TimeSeriesData data, String tableName) {
        return buildPoint(tableName, data.getTags(), data.getFields(),
                data.getTime(), TimeUnit.MILLISECONDS);
    }
}
