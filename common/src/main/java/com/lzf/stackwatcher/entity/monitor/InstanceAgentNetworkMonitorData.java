package com.lzf.stackwatcher.entity.monitor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class InstanceAgentNetworkMonitorData extends InstanceAgentBaseMonitorData {

    private String device;

    private int rxBytes;
    private int rxPackets;
    private int rxErrors;

    private int txBytes;
    private int txPackets;
    private int txErrors;

    public InstanceAgentNetworkMonitorData() { }

    public String getDevice() {
        return device;
    }

    public InstanceAgentNetworkMonitorData setDevice(String device) {
        this.device = device;
        return this;
    }

    public int getRxBytes() {
        return rxBytes;
    }

    public InstanceAgentNetworkMonitorData setRxBytes(int rxBytes) {
        this.rxBytes = rxBytes;
        return this;
    }

    public int getRxPackets() {
        return rxPackets;
    }

    public InstanceAgentNetworkMonitorData setRxPackets(int rxPackets) {
        this.rxPackets = rxPackets;
        return this;
    }

    public int getRxErrors() {
        return rxErrors;
    }

    public InstanceAgentNetworkMonitorData setRxErrors(int rxErrors) {
        this.rxErrors = rxErrors;
        return this;
    }

    public int getTxBytes() {
        return txBytes;
    }

    public InstanceAgentNetworkMonitorData setTxBytes(int txBytes) {
        this.txBytes = txBytes;
        return this;
    }

    public int getTxPackets() {
        return txPackets;
    }

    public InstanceAgentNetworkMonitorData setTxPackets(int txPackets) {
        this.txPackets = txPackets;
        return this;
    }

    public int getTxErrors() {
        return txErrors;
    }

    public InstanceAgentNetworkMonitorData setTxErrors(int txErrors) {
        this.txErrors = txErrors;
        return this;
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
        map.put("rx_errors", rxErrors);
        map.put("tx_bytes", txBytes);
        map.put("tx_packets", txPackets);
        map.put("tx_errors", txErrors);
        return map;
    }

    @Override
    public String toString() {
        return "InstanceAgentNetworkMonitorData{" +
                "device='" + device + '\'' +
                ", rxBytes=" + rxBytes +
                ", rxPackets=" + rxPackets +
                ", rxErrors=" + rxErrors +
                ", txBytes=" + txBytes +
                ", txPackets=" + txPackets +
                ", txErrors=" + txErrors +
                ", uuid='" + uuid + '\'' +
                ", time=" + time +
                '}';
    }
}
