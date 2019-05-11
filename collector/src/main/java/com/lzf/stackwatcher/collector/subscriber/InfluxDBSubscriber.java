package com.lzf.stackwatcher.collector.subscriber;

import com.lzf.stackwatcher.collector.Collector;
import com.lzf.stackwatcher.db.InfluxDBConnector;
import com.lzf.stackwatcher.db.InfluxDatabase;
import com.lzf.stackwatcher.entity.Data;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InfluxDBSubscriber extends PersistenceSubscriber {

    private final Collector collector;

    private final InfluxDatabase influxDB;

    private final InfluxDBConfig config;

    public InfluxDBSubscriber(Collector collector) {
        this.collector = collector;

        InfluxDBConfig cfg = new InfluxDBConfig(collector, collector.getZooKeeper());
        collector.registerConfig(cfg);

        this.influxDB = new InfluxDBConnector(cfg.getUsername(), cfg.getPassword(),
                String.format("http://%s:%d", cfg.getDbHost(), cfg.getPort()), cfg.getRetentionPolicy());

        this.config = cfg;
    }


    @Override
    public void receive(Data data) {
        TimeSeriesData tsd = (TimeSeriesData) data;

        String table = config.getTableName(tsd.getClass());
        if(table == null) {
            return;
        }

        influxDB.insert(config.getDbName(), table, tsd.getTags(), tsd.getFields(), tsd.getTime(), TimeUnit.MILLISECONDS);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void receive(List<Data> data) {
        if(data.size() == 0)
            return;

        List<TimeSeriesData> list = (List)data;
        String table = config.getTableName(list.get(0).getClass());

        if(table == null)
            return;

        BatchPoints bp = InfluxDatabase.buildBatchPoint(config.getDbName(), table, Collections.emptyMap(),
                config.getRetentionPolicy(), InfluxDB.ConsistencyLevel.ALL, list);

        influxDB.insertBatch(bp);
    }
}
