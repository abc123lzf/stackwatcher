package com.lzf.stackwatcher.entity;

public class Instance implements Data {
    private static final long serialVersionUID = -4205533290191430329L;

    private Integer id;
    private String name;
    private String uuid;
    private Integer novaUUId;

    public Instance() { }

    public Instance(String name, String uuid, Integer novaUUId) {
        this.name = name;
        this.uuid = uuid;
        this.novaUUId = novaUUId;
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

    public Integer getNovaUUId() {
        return novaUUId;
    }

    public void setNovaUUId(Integer novaUUId) {
        this.novaUUId = novaUUId;
    }
}