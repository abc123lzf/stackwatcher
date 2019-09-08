package com.lzf.stackwatcher.entity.monitor;

import java.util.HashMap;
import java.util.Map;

public class NovaStorageVolData extends NovaBaseMonitorData {

    private String name;

    private String belongPool;

    private long allocation;

    private long capacity;

    public NovaStorageVolData() {
    }

    public NovaStorageVolData(String host, long time) {
        super(host, time);
    }

    public String getName() {
        return name;
    }

    public NovaStorageVolData setName(String name) {
        this.name = name;
        return this;
    }

    public String getBelongPool() {
        return belongPool;
    }

    public NovaStorageVolData setBelongPool(String belongPool) {
        this.belongPool = belongPool;
        return this;
    }

    public long getAllocation() {
        return allocation;
    }

    public NovaStorageVolData setAllocation(long allocation) {
        this.allocation = allocation;
        return this;
    }

    public long getCapacity() {
        return capacity;
    }

    public NovaStorageVolData setCapacity(long capacity) {
        this.capacity = capacity;
        return this;
    }

    @Override
    public Map<String, String> getTags() {
        Map<String, String> map = new HashMap<>(4);
        map.put("host", host);
        map.put("name", name);
        map.put("pool_name", belongPool);
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
