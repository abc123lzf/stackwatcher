package com.lzf.stackwatcher.sentinel.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.nio.charset.Charset;

public class KafkaNode extends NodeInfo {

    public String host;

    public int port;

    public int jmxPort;

    private KafkaNode() { }

    public static KafkaNode parseZNodeString(byte[] content) {
        KafkaNode node = new KafkaNode();
        JSONObject obj = JSON.parseObject(new String(content, Charset.forName("UTF-8")));
        node.host = obj.getString("host");
        node.port = obj.getIntValue("port");
        node.jmxPort = obj.getIntValue("jmx_port");
        return node;
    }
}
