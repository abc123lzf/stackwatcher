package com.lzf.stackwatcher.alert.core;

import com.lzf.stackwatcher.alert.entity.Rule;

import java.util.Objects;

public class Data {

    private final String host;

    private final String device;

    private final int type;

    private final double value;

    private final long time;

    public Data(String host, int type, double value, long time) {
        this.host = Objects.requireNonNull(host);
        this.type = type;
        this.value = value;
        this.time = time;
        this.device = null;
    }

    public Data(String host, Rule.Type type, double value, long time) {
        this(host, type.key, value, time);
    }

    public Data(String host, Rule.Type type, String device, double value, long time) {
        this.host = Objects.requireNonNull(host);
        this.type = type.key;
        this.value = value;
        this.time = time;
        this.device = device;
    }

    public String getHost() {
        return host;
    }

    public int getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public long getTime() {
        return time;
    }

    public String getDevice() {
        return device;
    }
}
