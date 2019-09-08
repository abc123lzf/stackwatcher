package com.lzf.stackwatcher.entity;

public class Nova implements Data {

    private static final long serialVersionUID = -4205533290191430319L;

    private Integer id;
    private String name;
    private String uuid;
    private String host;

    public Nova() { }

    public Nova(String name, String uuid, String host) {
        this.name = name;
        this.uuid = uuid;
        this.host = host;
    }

    public Integer getId() {
        return id;
    }

    @Deprecated
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
