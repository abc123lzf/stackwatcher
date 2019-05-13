package com.lzf.stackwatcher.agent.data;

/**
 * 存储卷监控数据
 * @see org.libvirt.StorageVol
 * @see org.libvirt.StorageVolInfo
 */
public class StorageVolData extends CurrentNovaData {

    //存储卷名称
    public final String name;

    //所属的存储池名称
    public final String belongPool;

    //所属的存储池UUID
    public final String belongPoolUUID;

    //已使用的空间，单位字节
    public final long allocation;

    //总空间，单位字节
    public final long capacity;

    //存储卷的类型，有:VIR_STORAGE_VOL_FILE, VIR_STORAGE_VOL_BLOCK
    public final String type;

    public StorageVolData(String host, String name, String belongPool, String belongPoolUUID,
                          long allocation, long capacity, String type1) {
        super(host, Data.NOVA_STORAGE_VOL);
        this.name = name;
        this.belongPool = belongPool;
        this.belongPoolUUID = belongPoolUUID;
        this.allocation = allocation;
        this.capacity = capacity;
        this.type = type1;
    }
}
