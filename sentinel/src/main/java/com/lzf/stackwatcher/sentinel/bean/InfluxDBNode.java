package com.lzf.stackwatcher.sentinel.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.nio.charset.Charset;

public class InfluxDBNode extends NodeInfo {

    private String host;

    private int port;

    private String username;

    private String password;

    private InfluxDBNode() { }

    public static InfluxDBNode parseZNodeString(byte[] content) {
        JSONObject obj = JSON.parseObject(new String(content, Charset.forName("UTF-8")));
        InfluxDBNode node = new InfluxDBNode();
        node.host = obj.getString("host");
        node.port = obj.getIntValue("port");
        node.username = obj.getString("username");
        node.password = obj.getString("password");
        return node;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
