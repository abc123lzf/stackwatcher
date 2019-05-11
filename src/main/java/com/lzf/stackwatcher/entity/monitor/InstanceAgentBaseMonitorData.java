package com.lzf.stackwatcher.entity.monitor;

public abstract class InstanceAgentBaseMonitorData extends MonitorData {

    protected String host;
    protected String uuid;
    protected long time;

    protected InstanceAgentBaseMonitorData() { }

    protected InstanceAgentBaseMonitorData(String uuid, long time) {
        this.uuid = uuid;
        this.time = time;
    }

    public String getUuid() {
        return uuid;
    }

    public InstanceAgentBaseMonitorData setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public long getTime() {
        return time;
    }

    public InstanceAgentBaseMonitorData setTime(long time) {
        this.time = time;
        return this;
    }

    @Override
    public String getHost() {
        return host;
    }

    public InstanceAgentBaseMonitorData setHost(String host) {
        this.host = host;
        return this;
    }
}

