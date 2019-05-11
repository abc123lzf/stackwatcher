package com.lzf.stackwatcher.entity.monitor;

public abstract class InstanceBaseMonitorData extends MonitorData {

    protected String host;    //所属实例的物理机主机名
    protected String uuid;     //所属的实例ID
    protected long time;     //数据记录时间

    protected InstanceBaseMonitorData() { }

    protected InstanceBaseMonitorData(String uuid, long time) {
        this.uuid = uuid;
        this.time = time;
    }


    public String getUuid() {
        return uuid;
    }

    public InstanceBaseMonitorData setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public long getTime() {
        return time;
    }

    public InstanceBaseMonitorData setTime(long time) {
        this.time = time;
        return this;
    }

    @Override
    public String getHost() {
        return host;
    }

    public InstanceBaseMonitorData setHost(String host) {
        this.host = host;
        return this;
    }
}
