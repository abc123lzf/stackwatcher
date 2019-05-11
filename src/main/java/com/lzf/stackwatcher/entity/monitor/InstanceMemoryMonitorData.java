package com.lzf.stackwatcher.entity.monitor;

import java.util.HashMap;
import java.util.Map;

public class InstanceMemoryMonitorData extends InstanceBaseMonitorData {

    private Long used;
    private Long size;

    public InstanceMemoryMonitorData() { }

    public Long getUsed() {
        return used;
    }

    public InstanceMemoryMonitorData setUsed(Long used) {
        this.used = used;
        return this;
    }

    public Long getSize() {
        return size;
    }

    public InstanceMemoryMonitorData setSize(Long size) {
        this.size = size;
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
        Map<String, Object> map = new HashMap<>(4);
        map.put("used", used);
        map.put("size", size);
        return map;
    }
}
