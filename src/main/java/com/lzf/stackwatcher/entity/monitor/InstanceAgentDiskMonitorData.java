package com.lzf.stackwatcher.entity.monitor;

import java.util.HashMap;
import java.util.Map;

public class InstanceAgentDiskMonitorData extends InstanceAgentBaseMonitorData {
    private String device;

    private long used;			//用量
    private float utilization;	//使用量(单位:百分比)
    private long free;			//剩余空间
    private long total;			//总空间

    private long rdBytes;		//每秒读入字节数
    private long rdReq;			//读入IOPS
    private long wrBytes;		//每秒写入字节数
    private long wrReq;			//写入IOPS

    public InstanceAgentDiskMonitorData() { }

    public String getDevice() {
        return device;
    }

    public InstanceAgentDiskMonitorData setDevice(String device) {
        this.device = device;
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
        Map<String, Object> map = new HashMap<>(16);
        map.put("used", used);
        map.put("utilization", utilization);
        map.put("free", free);
        map.put("total", total);
        map.put("rd_bytes", rdBytes);
        map.put("rd_req", rdReq);
        map.put("wr_bytes", wrBytes);
        map.put("wr_req", wrReq);
        return map;
    }

    public long getUsed() {
        return used;
    }

    public InstanceAgentDiskMonitorData setUsed(long used) {
        this.used = used;
        return this;
    }

    public float getUtilization() {
        return utilization;
    }

    public InstanceAgentDiskMonitorData setUtilization(float utilization) {
        this.utilization = utilization;
        return this;
    }

    public long getFree() {
        return free;
    }

    public InstanceAgentDiskMonitorData setFree(long free) {
        this.free = free;
        return this;
    }

    public long getTotal() {
        return total;
    }

    public InstanceAgentDiskMonitorData setTotal(long total) {
        this.total = total;
        return this;
    }

    public long getRdBytes() {
        return rdBytes;
    }

    public InstanceAgentDiskMonitorData setRdBytes(long rdBytes) {
        this.rdBytes = rdBytes;
        return this;
    }

    public long getRdReq() {
        return rdReq;
    }

    public InstanceAgentDiskMonitorData setRdReq(long rdReq) {
        this.rdReq = rdReq;
        return this;
    }

    public long getWrBytes() {
        return wrBytes;
    }

    public InstanceAgentDiskMonitorData setWrBytes(long wrBytes) {
        this.wrBytes = wrBytes;
        return this;
    }

    public long getWrReq() {
        return wrReq;
    }

    public InstanceAgentDiskMonitorData setWrReq(long wrReq) {
        this.wrReq = wrReq;
        return this;
    }

    @Override
    public String toString() {
        return "InstanceAgentDiskMonitorData{" +
                "device='" + device + '\'' +
                ", used=" + used +
                ", utilization=" + utilization +
                ", free=" + free +
                ", total=" + total +
                ", rdBytes=" + rdBytes +
                ", rdReq=" + rdReq +
                ", wrBytes=" + wrBytes +
                ", wrReq=" + wrReq +
                ", uuid='" + uuid + '\'' +
                ", time=" + time +
                '}';
    }
}
