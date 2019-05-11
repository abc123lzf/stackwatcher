package com.lzf.stackwatcher.entity.monitor;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * 物理机CPU监控数据
 * RowKey结构：24字节(16字节虚拟机UUID + 8字节数据采集时间)
 * +--------------+---------+
 * |   Nova UUID  |Timestamp|
 * |   (16 Byte)  | (8 Byte)|
 * +--------------+---------+
 */
public class NovaCPUMonitorData extends NovaBaseMonitorData {
    private Float total;
    private Float system;
    private Float iowait;
    private Float user;
    private Float other;

    public NovaCPUMonitorData() {
        super();
    }

    public Float getTotal() {
        return total;
    }

    public NovaCPUMonitorData setTotal(Float total) {
        this.total = total;
        return this;
    }

    public Float getSystem() {
        return system;
    }

    public NovaCPUMonitorData setSystem(Float system) {
        this.system = system;
        return this;
    }

    public Float getIowait() {
        return iowait;
    }

    public NovaCPUMonitorData setIowait(Float iowait) {
        this.iowait = iowait;
        return this;
    }

    public Float getUser() {
        return user;
    }

    public NovaCPUMonitorData setUser(Float user) {
        this.user = user;
        return this;
    }

    public Float getOther() {
        return other;
    }

    public NovaCPUMonitorData setOther(Float other) {
        this.other = other;
        return this;
    }


    @Override
    public Map<String, String> getTags() {
        Map<String, String> tags = new HashMap<>(2);
        tags.put("host", host);
        return tags;
    }

    @Override
    public Map<String, Object> getFields() {
        Map<String, Object> fields = new HashMap<>(8);
        fields.put("total", total);
        fields.put("system", system);
        fields.put("user", user);
        fields.put("iowait", iowait);
        fields.put("other", other);
        return fields;
    }

    @Override
    public String toString() {
        return "NovaCPUMonitorData{" +
                "total=" + total +
                ", system=" + system +
                ", iowait=" + iowait +
                ", user=" + user +
                ", other=" + other +
                ", host='" + host + '\'' +
                ", time=" + time +
                '}';
    }
}
