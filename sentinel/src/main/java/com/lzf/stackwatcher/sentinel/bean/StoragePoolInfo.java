package com.lzf.stackwatcher.sentinel.bean;

class StoragePoolInfo {

    //存储池名称
    public final String name;

    //存储池UUID
    public final String uuid;

    //已用空间，单位字节
    public final long allocation;

    //可用空间，单位字节
    public final long available;

    //总空间，单位字节
    public final long capacity;

    /**
     * 存储池状态，有4种类型：
     * VIR_STORAGE_POOL_INACTIVE
     * VIR_STORAGE_POOL_BUILDING
     * VIR_STORAGE_POOL_RUNNING
     * VIR_STORAGE_POOL_DEGRADED
     */
    public final String status;

    public StoragePoolInfo(String name, String uuid, long allocation,
                           long available, long capacity, String status) {
        this.name = name;
        this.uuid = uuid;
        this.allocation = allocation;
        this.available = available;
        this.capacity = capacity;
        this.status = status;
    }

}
