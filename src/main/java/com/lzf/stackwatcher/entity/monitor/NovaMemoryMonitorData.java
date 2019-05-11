package com.lzf.stackwatcher.entity.monitor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 物理机内存监控数据
 * RowKey结构：24字节(16字节虚拟机UUID + 8字节数据采集时间)
 * +--------------+---------+
 * |   Nova UUID  |Timestamp|
 * |   (16 Byte)  | (8 Byte)|
 * +--------------+---------+
 */
public class NovaMemoryMonitorData extends NovaBaseMonitorData {
    //内存用量，单位KB
    private Long usage;

    //内存大小，单位KB
    private Long size;

    //虚拟内存用量
    private Long swapUsage;

    //虚拟内存大小
    private Long swapSize;

    public NovaMemoryMonitorData() { }

    public Long getUsage() {
        return usage;
    }

    public NovaMemoryMonitorData setUsage(Long usage) {
        this.usage = usage;
        return this;
    }

    public Long getSize() {
        return size;
    }

    public NovaMemoryMonitorData setSize(Long size) {
        this.size = size;
        return this;
    }

    public Long getSwapUsage() {
        return swapUsage;
    }

    public NovaMemoryMonitorData setSwapUsage(Long swapUsage) {
        this.swapUsage = swapUsage;
        return this;
    }

    public Long getSwapSize() {
        return swapSize;
    }

    public NovaMemoryMonitorData setSwapSize(Long swapSize) {
        this.swapSize = swapSize;
        return this;
    }


    @Override
    public Map<String, String> getTags() {
        Map<String, String> map = new HashMap<>(2);
        map.put("host", host);
        return map;
    }

    @Override
    public Map<String, Object> getFields() {
        Map<String, Object> map = new HashMap<>(8);
        map.put("usage", usage);
        map.put("size", size);
        map.put("swap_size", swapSize);
        map.put("swap_usage", swapUsage);
        return map;
    }

    @Override
    public String toString() {
        return "NovaMemoryMonitorData{" +
                "usage=" + usage +
                ", size=" + size +
                ", host='" + host + '\'' +
                ", time=" + time +
                '}';
    }
}
