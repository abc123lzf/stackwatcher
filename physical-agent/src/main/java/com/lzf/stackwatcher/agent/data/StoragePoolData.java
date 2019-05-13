package com.lzf.stackwatcher.agent.data;

/**
 * 存储池监控数据
 * @see org.libvirt.StoragePool
 * @see org.libvirt.StoragePoolInfo
 */
public class StoragePoolData extends CurrentNovaData {

    //存储池名称
    public final String name;

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

    public StoragePoolData(String host, String name, String uuid, long allocation,
                           long available, long capacity, String status) {
        super(host, NOVA_STORAGE_POOL);
        this.name = name;
        this.uuid = uuid;
        this.allocation = allocation;
        this.available = available;
        this.capacity = capacity;
        this.status = status;
    }
}
