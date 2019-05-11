package com.lzf.stackwatcher.entity.monitor;

import java.util.HashMap;
import java.util.Map;

/**
 * 计算节点磁盘IO监控数据
 *
 */
public class NovaDiskIOMonitorData extends NovaBaseMonitorData {

    private String device;

    private Integer rdBytes;
    private Integer rdReq;

    private Integer wrBytes;
    private Integer wrReq;

    @Override
    public Map<String, String> getTags() {
        Map<String, String> map = new HashMap<>(4);
        map.put("host", host);
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

    public NovaDiskIOMonitorData() { }

    public String getDevice() {
        return device;
    }

    public NovaDiskIOMonitorData setDevice(String deviceName) {
        this.device = deviceName;
        return this;
    }

    public Integer getRdBytes() {
        return rdBytes;
    }

    public NovaDiskIOMonitorData setRdBytes(Integer rdBytes) {
        this.rdBytes = rdBytes;
        return this;
    }

    public Integer getRdReq() {
        return rdReq;
    }

    public NovaDiskIOMonitorData setRdReq(Integer rdReq) {
        this.rdReq = rdReq;
        return this;
    }

    public Integer getWrBytes() {
        return wrBytes;
    }

    public NovaDiskIOMonitorData setWrBytes(Integer wrBytes) {
        this.wrBytes = wrBytes;
        return this;
    }

    public Integer getWrReq() {
        return wrReq;
    }

    public NovaDiskIOMonitorData setWrReq(Integer wrReq) {
        this.wrReq = wrReq;
        return this;
    }


    @Override
    public String toString() {
        return "NovaDiskIOMonitorData{" +
                "device='" + device + '\'' +
                ", rdBytes=" + rdBytes +
                ", rdReq=" + rdReq +
                ", wrBytes=" + wrBytes +
                ", wrReq=" + wrReq +
                ", host='" + host + '\'' +
                ", time=" + time +
                '}';
    }
}
