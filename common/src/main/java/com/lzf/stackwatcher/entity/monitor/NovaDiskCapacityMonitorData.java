package com.lzf.stackwatcher.entity.monitor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 物理机磁盘容量监控数据
 *
 */
public class NovaDiskCapacityMonitorData extends NovaBaseMonitorData {
    private String device;
    private long size;
    private long usage;

    public NovaDiskCapacityMonitorData() { }

    public NovaDiskCapacityMonitorData(String host, long time, String device, long size, long usage) {
        super(host, time);
        this.device = device;
        this.size = size;
        this.usage = usage;
    }


    @Override
    public Map<String, String> getTags() {
        Map<String, String> map = new HashMap<>(4);
        map.put("host", host);
        map.put("device_name", device);
        return map;
    }

    @Override
    public Map<String, Object> getFields() {
        Map<String, Object> map = new HashMap<>(4);
        map.put("usage", usage);
        map.put("size", size);
        return map;
    }

    public String getDevice() {
        return device;
    }

    public NovaDiskCapacityMonitorData setDevice(String device) {
        this.device = device;
        return this;
    }

    public long getSize() {
        return size;
    }

    public NovaDiskCapacityMonitorData setSize(long size) {
        this.size = size;
        return this;
    }

    public long getUsage() {
        return usage;
    }

    public NovaDiskCapacityMonitorData setUsage(long usage) {
        this.usage = usage;
        return this;
    }


    @Override
    public String toString() {
        return "NovaDiskCapacityMonitorData{" +
                "device='" + device + '\'' +
                ", size=" + size +
                ", usage=" + usage +
                ", host='" + host + '\'' +
                ", time=" + time +
                '}';
    }
}
