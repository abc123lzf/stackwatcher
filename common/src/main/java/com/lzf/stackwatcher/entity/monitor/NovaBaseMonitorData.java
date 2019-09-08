package com.lzf.stackwatcher.entity.monitor;

public abstract class NovaBaseMonitorData extends MonitorData {

    protected String host;
    protected long time;

    protected NovaBaseMonitorData() { }

    protected NovaBaseMonitorData(String host, long time) {
        this.host = host;
        this.time = time;
    }


    public long getTime() {
        return time;
    }

    public NovaBaseMonitorData setTime(long time) {
        this.time = time;
        return this;
    }

    public String getHost() {
        return host;
    }

    public NovaBaseMonitorData setHost(String host) {
        this.host = host;
        return this;
    }

}
