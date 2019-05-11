package com.lzf.stackwatcher.entity.monitor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 虚拟机CPU使用监控数据
 * RowKey结构：24字节(16字节虚拟机UUID + 8字节数据采集时间)
 * +--------------+---------+
 * |Instance UUID |Timestamp|
 * |  (16 Byte)   | (8 Byte)|
 * +--------------+---------+
 */
public class InstanceCPUMonitorData extends InstanceBaseMonitorData {
    private static final long serialVersionUID = -5205533290191430419L;

    private Float usage;       //使用率

    public InstanceCPUMonitorData() { }

    public InstanceCPUMonitorData(String uuid, long time, Float usage) {
        super(uuid, time);
        this.usage = usage;
    }

    public Float getUsage() {
        return usage;
    }

    public InstanceCPUMonitorData setUsage(Float usage) {
        this.usage = usage;
        return this;
    }

    @Override
    public Map<String, String> getTags() {
        Map<String, String> tags = new HashMap<>(4);
        tags.put("host", host);
        tags.put("instance_uuid", uuid);
        return tags;
    }

    @Override
    public Map<String, Object> getFields() {
        Map<String, Object> fields = new HashMap<>(2);
        fields.put("usage", usage);
        return fields;
    }

    @Override
    public String toString() {
        return "InstanceCPUMonitorData{" +
                "usage=" + usage +
                ", uuid='" + uuid + '\'' +
                ", time=" + time +
                '}';
    }
}
