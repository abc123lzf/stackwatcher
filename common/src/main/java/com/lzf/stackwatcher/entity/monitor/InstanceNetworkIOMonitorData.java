package com.lzf.stackwatcher.entity.monitor;

import java.util.HashMap;
import java.util.Map;

/**
 * 虚拟机网络IO监控数据，存储在HBase数据库中，每个InstanceNetworkIOMointorData对象
 * 对应一个虚拟机其中一个虚拟机网络的监控数据。
 * RowKey结构：40字节 (16字节虚拟机UUID + 16字节Libvirt虚拟网络UUID + 8字节数据采集时间)
 * +--------------+------------+---------+
 * |Instance UUID |Network UUID|Timestamp|
 * |  (16 Byte)   | (16 Byte)  | (8 Byte)|
 * +--------------+------------+---------+
 *
 */
public class InstanceNetworkIOMonitorData extends InstanceBaseMonitorData {

    private String device;
    private String deviceUUID;

    private Integer rxBytes;
    private Integer rxPackets;

    private Integer txBytes;
    private Integer txPackets;

    public InstanceNetworkIOMonitorData() { }

    public InstanceNetworkIOMonitorData(String insUUId, long time, String device, Integer rxBytes,
                                        Integer rxPackets, Integer txBytes, Integer txPackets) {
        super(insUUId, time);
        this.device = device;
        this.rxBytes = rxBytes;
        this.rxPackets = rxPackets;
        this.txBytes = txBytes;
        this.txPackets = txPackets;
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
        map.put("rx_bytes", rxBytes);
        map.put("rx_packets", rxPackets);
        map.put("tx_bytes", txBytes);
        map.put("tx_packets", txPackets);
        return map;
    }

    public String getDevice() {
        return device;
    }

    public InstanceNetworkIOMonitorData setDevice(String device) {
        this.device = device;
        return this;
    }

    public Integer getRxBytes() {
        return rxBytes;
    }

    public InstanceNetworkIOMonitorData setRxBytes(Integer rxBytes) {
        this.rxBytes = rxBytes;
        return this;
    }

    public Integer getRxPackets() {
        return rxPackets;
    }

    public InstanceNetworkIOMonitorData setRxPackets(Integer rxPackets) {
        this.rxPackets = rxPackets;
        return this;
    }

    public Integer getTxBytes() {
        return txBytes;
    }

    public InstanceNetworkIOMonitorData setTxBytes(Integer txBytes) {
        this.txBytes = txBytes;
        return this;
    }

    public Integer getTxPackets() {
        return txPackets;
    }

    public String getDeviceUUID() {
        return deviceUUID;
    }

    public InstanceNetworkIOMonitorData setDeviceUUID(String deviceUUID) {
        this.deviceUUID = deviceUUID;
        return this;
    }

    public InstanceNetworkIOMonitorData setTxPackets(Integer txPackets) {
        this.txPackets = txPackets;
        return this;
    }

    @Override
    public String toString() {
        return "InstanceNetworkIOMonitorData{" +
                "device='" + device + '\'' +
                ", deviceUUID='" + deviceUUID + '\'' +
                ", rxBytes=" + rxBytes +
                ", rxPackets=" + rxPackets +
                ", txBytes=" + txBytes +
                ", txPackets=" + txPackets +
                ", uuid='" + uuid + '\'' +
                ", time=" + time +
                '}';
    }
}
