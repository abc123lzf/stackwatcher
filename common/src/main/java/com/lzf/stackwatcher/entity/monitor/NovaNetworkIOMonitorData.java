package com.lzf.stackwatcher.entity.monitor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 物理机网卡监控数据
 * RowKey格式如下：32字节(16字节虚拟机UUID)
 * +------------+----------+---------+
 * |  Nova UUID |DeviceName|Timestamp|
 * |  (16Byte)  |  (8Byte) | (8Byte) |
 * +------------+----------+---------+
 */
public class NovaNetworkIOMonitorData extends NovaBaseMonitorData {

    private String device;

    private Long rxBytes;
    private Long rxPackets;
    private Long rxDrop;

    private Long txBytes;
    private Long txPackets;
    private Long txDrop;

    private Integer rxByteSpeed;
    private Integer rxPacketSpeed;

    private Integer txByteSpeed;
    private Integer txPacketSpeed;

    public NovaNetworkIOMonitorData() { }

    @Override
    public Map<String, String> getTags() {
        Map<String, String> map = new HashMap<>(4);
        map.put("host", host);
        map.put("device_name", device);
        return map;
    }

    @Override
    public Map<String, Object> getFields() {
        Map<String, Object> map = new HashMap<>(16);
        map.put("rx_bytes", rxBytes);
        map.put("rx_packets", rxPackets);
        map.put("rx_drop", rxDrop);
        map.put("tx_bytes", txBytes);
        map.put("tx_packets", txPackets);
        map.put("tx_drop", txDrop);
        map.put("rx_byte_speed", rxByteSpeed);
        map.put("rx_packet_speed", rxPacketSpeed);
        map.put("tx_byte_speed", txByteSpeed);
        map.put("tx_packet_speed", txPacketSpeed);
        return map;
    }

    public String getDevice() {
        return device;
    }

    public NovaNetworkIOMonitorData setDevice(String device) {
        this.device = device;
        return this;
    }

    public Long getRxBytes() {
        return rxBytes;
    }

    public NovaNetworkIOMonitorData setRxBytes(Long rxBytes) {
        this.rxBytes = rxBytes;
        return this;
    }

    public Long getRxPackets() {
        return rxPackets;
    }

    public NovaNetworkIOMonitorData setRxPackets(Long rxPackets) {
        this.rxPackets = rxPackets;
        return this;
    }

    public Long getTxBytes() {
        return txBytes;
    }

    public NovaNetworkIOMonitorData setTxBytes(Long txBytes) {
        this.txBytes = txBytes;
        return this;
    }

    public Long getTxPackets() {
        return txPackets;
    }

    public NovaNetworkIOMonitorData setTxPackets(Long txPackets) {
        this.txPackets = txPackets;
        return this;
    }

    public Long getRxDrop() {
        return rxDrop;
    }

    public NovaNetworkIOMonitorData setRxDrop(Long rxDrop) {
        this.rxDrop = rxDrop;
        return this;
    }

    public Long getTxDrop() {
        return txDrop;
    }

    public NovaNetworkIOMonitorData setTxDrop(Long txDrop) {
        this.txDrop = txDrop;
        return this;
    }

    public Integer getRxByteSpeed() {
        return rxByteSpeed;
    }

    public NovaNetworkIOMonitorData setRxByteSpeed(Integer rxByteSpeed) {
        this.rxByteSpeed = rxByteSpeed;
        return this;
    }

    public Integer getRxPacketSpeed() {
        return rxPacketSpeed;
    }

    public NovaNetworkIOMonitorData setRxPacketSpeed(Integer rxPacketSpeed) {
        this.rxPacketSpeed = rxPacketSpeed;
        return this;
    }

    public Integer getTxByteSpeed() {
        return txByteSpeed;
    }

    public NovaNetworkIOMonitorData setTxByteSpeed(Integer txByteSpeed) {
        this.txByteSpeed = txByteSpeed;
        return this;
    }

    public Integer getTxPacketSpeed() {
        return txPacketSpeed;
    }

    public NovaNetworkIOMonitorData setTxPacketSpeed(Integer txPacketSpeed) {
        this.txPacketSpeed = txPacketSpeed;
        return this;
    }

    @Override
    public String toString() {
        return "NovaNetworkIOMonitorData{" +
                "device='" + device + '\'' +
                ", rxBytes=" + rxBytes +
                ", rxPackets=" + rxPackets +
                ", txBytes=" + txBytes +
                ", txPackets=" + txPackets +
                ", host='" + host + '\'' +
                ", time=" + time +
                '}';
    }
}
