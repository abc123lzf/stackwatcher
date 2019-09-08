package com.lzf.stackwatcher.entity.monitor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class InstanceAgentCPUMonitorData extends InstanceAgentBaseMonitorData {
    public static final String DEFAULT_TABLE_NAME = "instance_agent_cpu";

    private float idle;			//当前空闲CPU百分比
    private float system;		//当前内核空间占用CPU百分比
    private float iowait;		//当前等待IO操作的CPU百分比
    private float user;			//当前用户空间占用CPU百分比
    private float other;		//其他占用CPU百分比,
    private float totalUsed;	//当前消耗的总CPU百分比

    public InstanceAgentCPUMonitorData() { }

    public float getIdle() {
        return idle;
    }

    public InstanceAgentCPUMonitorData setIdle(float idle) {
        this.idle = idle;
        return this;
    }

    public float getSystem() {
        return system;
    }

    public InstanceAgentCPUMonitorData setSystem(float system) {
        this.system = system;
        return this;
    }

    public float getIowait() {
        return iowait;
    }

    public InstanceAgentCPUMonitorData setIowait(float iowait) {
        this.iowait = iowait;
        return this;
    }

    public float getUser() {
        return user;
    }

    public InstanceAgentCPUMonitorData setUser(float user) {
        this.user = user;
        return this;
    }

    public float getOther() {
        return other;
    }

    public InstanceAgentCPUMonitorData setOther(float other) {
        this.other = other;
        return this;
    }

    public float getTotalUsed() {
        return totalUsed;
    }

    public InstanceAgentCPUMonitorData setTotalUsed(float totalUsed) {
        this.totalUsed = totalUsed;
        return this;
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
        map.put("idle", idle);
        map.put("system", system);
        map.put("iowait", iowait);
        map.put("user", user);
        map.put("other", other);
        map.put("total", totalUsed);
        return map;
    }

    @Override
    public String toString() {
        return "InstanceAgentCPUMonitorData{" +
                "idle=" + idle +
                ", system=" + system +
                ", iowait=" + iowait +
                ", user=" + user +
                ", other=" + other +
                ", totalUsed=" + totalUsed +
                ", uuid='" + uuid + '\'' +
                ", time=" + time +
                '}';
    }
}
