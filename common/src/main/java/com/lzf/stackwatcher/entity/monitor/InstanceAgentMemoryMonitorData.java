package com.lzf.stackwatcher.entity.monitor;

import java.util.HashMap;
import java.util.Map;

public class InstanceAgentMemoryMonitorData extends InstanceAgentBaseMonitorData {

    private long total;				//内存总量
    private long used;				//已用内存量
    private long actualUsed;		//用户实际使用的内存
    private long free;				//剩余内存量
    private float freeUtilization;	//剩余内存百分比
    private float usedUtilization;	//内存使用率

    public InstanceAgentMemoryMonitorData() { }


    public long getTotal() {
        return total;
    }

    public InstanceAgentMemoryMonitorData setTotal(long total) {
        this.total = total;
        return this;
    }

    public long getUsed() {
        return used;
    }

    public InstanceAgentMemoryMonitorData setUsed(long used) {
        this.used = used;
        return this;
    }

    public long getActualUsed() {
        return actualUsed;
    }

    public InstanceAgentMemoryMonitorData setActualUsed(long actualUsed) {
        this.actualUsed = actualUsed;
        return this;
    }

    public long getFree() {
        return free;
    }

    public InstanceAgentMemoryMonitorData setFree(long free) {
        this.free = free;
        return this;
    }

    public float getFreeUtilization() {
        return freeUtilization;
    }

    public InstanceAgentMemoryMonitorData setFreeUtilization(float freeUtilization) {
        this.freeUtilization = freeUtilization;
        return this;
    }

    public float getUsedUtilization() {
        return usedUtilization;
    }

    public InstanceAgentMemoryMonitorData setUsedUtilization(float usedUtilization) {
        this.usedUtilization = usedUtilization;
        return this;
    }

    @Override
    public String toString() {
        return "InstanceAgentMemoryMonitorData{" +
                "total=" + total +
                ", used=" + used +
                ", actualUsed=" + actualUsed +
                ", free=" + free +
                ", freeUtilization=" + freeUtilization +
                ", usedUtilization=" + usedUtilization +
                ", uuid='" + uuid + '\'' +
                ", time=" + time +
                '}';
    }


    @Override
    public Map<String, String> getTags() {
        Map<String, String> map = new HashMap<>(4);
        map.put("host", host);
        map.put("instance_uuid", uuid);
        return map;
    }

    @Override
    public Map<String, Object> getFields() {
        Map<String, Object> map = new HashMap<>(8);
        map.put("total", total);
        map.put("used", used);
        map.put("actual_used", actualUsed);
        map.put("free", free);
        map.put("free_utilization", freeUtilization);
        map.put("used_utilization", usedUtilization);
        return map;
    }
}
