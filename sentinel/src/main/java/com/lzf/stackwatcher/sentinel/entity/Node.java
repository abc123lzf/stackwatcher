package com.lzf.stackwatcher.sentinel.entity;

public class Node {

    public static final int TYPE_AGENT = 0;
    public static final int TYPE_COLLECTOR = 1;
    public static final int TYPE_KAFKA = 2;
    public static final int TYPE_INFLUXDB = 3;

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 主机名
     */
    private String hostname;

    /**
     * IP地址(可选)
     */
    private String ipAddress;

    /**
     * 节点类型，其中：
     * 0代表Agent节点
     * 1代表Collector节点
     * 2代表Kafka节点
     * 3代表InfluxDB节点
     */
    private Integer type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname == null ? null : hostname.trim();
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress == null ? null : ipAddress.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}