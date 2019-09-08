package com.lzf.stackwatcher.entity.monitor;

import com.lzf.stackwatcher.entity.TimeSeriesData;

public abstract class MonitorData implements TimeSeriesData {

    /**
     * @return 该监控数据源的主机名
     */
    public abstract String getHost();

    /**
     * @return 监控数据采集时间
     */
    public abstract long getTime();

    protected MonitorData() { }
}
