package com.lzf.stackwatcher.entity.monitor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 磁盘IO监控数据
 * RowKey结构如下：32字节(16字节虚拟机UUID，8字节设备名(不足用0补齐，多余取前8个字符)，8字节数据记录时间)
 * +------------+----------+---------+
 * |InstanceUUID|DeviceName|Timestamp|
 * |  (16Byte)  |  (8Byte) | (8Byte) |
 * +------------+----------+---------+
 */
public class InstanceDiskIOMonitorData extends InstanceBaseMonitorData {
    private String device;
    private String deviceUUID;

    private Integer rdBytes;
    private Integer rdReq;
    private Integer wrBytes;
    private Integer wrReq;

    public InstanceDiskIOMonitorData() { }

    public InstanceDiskIOMonitorData(String insId, long time, String device, Integer rdBytes,
                                     Integer rdReq, Integer wrBytes, Integer wrReq) {
        super(insId, time);
        this.device = device;
        this.rdBytes = rdBytes;
        this.rdReq = rdReq;
        this.wrBytes = wrBytes;
        this.wrReq = wrReq;
    }

    @Override
    public Map<String, String> getTags() {
        Map<String, String> map = new HashMap<>(4);
        map.put("host", host);
        map.put("instance_uuid", uuid);
        map.put("device_name", device);
        return map;
    }

    @Override
    public Map<String, Object> getFields() {
        Map<String, Object> map = new HashMap<>(8);
        map.put("rd_bytes", rdBytes);
        map.put("rd_req", rdReq);
        map.put("wr_bytes", wrBytes);
        map.put("wr_req", wrReq);
        return map;
    }

    public String getDevice() {
        return device;
    }

    public InstanceDiskIOMonitorData setDevice(String device) {
        this.device = device;
        return this;
    }

    public String getDeviceUUID() {
        return deviceUUID;
    }

    public InstanceDiskIOMonitorData setDeviceUUID(String deviceUUID) {
        this.deviceUUID = deviceUUID;
        return this;
    }

    public Integer getRdBytes() {
        return rdBytes;
    }

    public InstanceDiskIOMonitorData setRdBytes(Integer rdBytes) {
        this.rdBytes = rdBytes;
        return this;
    }

    public Integer getRdReq() {
        return rdReq;
    }

    public InstanceDiskIOMonitorData setRdReq(Integer rdReq) {
        this.rdReq = rdReq;
        return this;
    }

    public Integer getWrBytes() {
        return wrBytes;
    }

    public InstanceDiskIOMonitorData setWrBytes(Integer wrBytes) {
        this.wrBytes = wrBytes;
        return this;
    }

    public Integer getWrReq() {
        return wrReq;
    }

    public InstanceDiskIOMonitorData setWrReq(Integer wrReq) {
        this.wrReq = wrReq;
        return this;
    }

    @Override
    public String toString() {
        return "InstanceDiskIOMonitorData{" +
                "device='" + device + '\'' +
                ", deviceUUID='" + deviceUUID + '\'' +
                ", rdBytes=" + rdBytes +
                ", rdReq=" + rdReq +
                ", wrBytes=" + wrBytes +
                ", wrReq=" + wrReq +
                ", uuid='" + uuid + '\'' +
                ", time=" + time +
                '}';
    }
}
