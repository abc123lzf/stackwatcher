package com.lzf.stackwatcher.entity.monitor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 磁盘容量监控数据
 * RowKey结构如下：32字节(16字节虚拟机UUID，8字节设备名(不足用0补齐，多余取前8个字符)，8字节数据记录时间)
 * +------------+----------+---------+
 * |InstanceUUID|DeviceName|Timestamp|
 * |  (16Byte)  |  (8Byte) | (8Byte) |
 * +------------+----------+---------+
 */
public class InstanceDiskCapacityMonitorData extends InstanceBaseMonitorData {

    private String device;
    private Long usage;
    private Long size;

    public InstanceDiskCapacityMonitorData() { }

    public InstanceDiskCapacityMonitorData(String uuid, long time, String device,
                                           Long usage, Long size) {
        super(uuid, time);
        this.device = device;
        this.usage = usage;
        this.size = size;
    }

    public String getDevice() {
        return device;
    }

    public InstanceDiskCapacityMonitorData setDevice(String device) {
        this.device = device;
        return this;
    }

    public Long getUsage() {
        return usage;
    }

    public InstanceDiskCapacityMonitorData setUsage(Long usage) {
        this.usage = usage;
        return this;
    }

    public Long getSize() {
        return size;
    }

    public InstanceDiskCapacityMonitorData setSize(Long size) {
        this.size = size;
        return this;
    }

    @Override
    public String toString() {
        return "InstanceDiskCapacityMonitorData{" +
                "device='" + device + '\'' +
                ", usage=" + usage +
                ", size=" + size +
                ", uuid='" + uuid + '\'' +
                ", time=" + time +
                '}';
    }

    @Override
    public Map<String, String> getTags() {
        Map<String, String> map = new HashMap<>(4);
        map.put("host", host);
        map.put("instance_uuid", uuid);
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
}
