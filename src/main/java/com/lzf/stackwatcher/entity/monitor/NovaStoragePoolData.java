package com.lzf.stackwatcher.entity.monitor;

import java.util.HashMap;
import java.util.Map;

public class NovaStoragePoolData extends NovaBaseMonitorData {

    private String name;

    private String uuid;

    private long allocation;

    private long capacity;

    public NovaStoragePoolData() {
    }

    public NovaStoragePoolData(String host, long time) {
        super(host, time);
    }

    public String getName() {
        return name;
    }

    public NovaStoragePoolData setName(String name) {
        this.name = name;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public NovaStoragePoolData setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public long getAllocation() {
        return allocation;
    }

    public NovaStoragePoolData setAllocation(long allocation) {
        this.allocation = allocation;
        return this;
    }

    public long getCapacity() {
        return capacity;
    }

    public NovaStoragePoolData setCapacity(long capacity) {
        this.capacity = capacity;
        return this;
    }

    @Override
    public Map<String, String> getTags() {
        Map<String, String> map = new HashMap<>(4);
        map.put("host", host);
        map.put("name", name);
        return map;
    }

    @Override
    public Map<String, Object> getFields() {
        Map<String, Object> map = new HashMap<>(4);
        map.put("allocation", allocation);
        map.put("capacity", capacity);
        return map;
    }
}
