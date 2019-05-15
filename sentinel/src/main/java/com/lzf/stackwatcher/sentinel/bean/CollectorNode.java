package com.lzf.stackwatcher.sentinel.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.nio.charset.Charset;

public class CollectorNode extends NodeInfo {

    public String host;

    public String kafkaAddresses;

    public String kafkaGroup;

    public Database database;

    public static final class Database {
        public String type;
        public String host;
        public int port;
        public String dbName;
        public String username;
        public String password;

        private Database() { }
    }

    private CollectorNode() { }

    public static CollectorNode parseZNodeString(byte[] content) {
        CollectorNode node = new CollectorNode();
        JSONObject obj = JSON.parseObject(new String(content, Charset.forName("UTF-8")));
        node.host = obj.getString("host");

        node.kafkaAddresses = obj.getString("using-kafka-addresses");
        node.kafkaGroup = obj.getString("kafka-group-id");

        JSONObject dbObj = obj.getJSONObject("persistence");
        Database db = new Database();
        db.type = obj.getString("persistence-db");
        db.host = dbObj.getString("host");
        db.port = dbObj.getIntValue("port");
        db.dbName = dbObj.getString("dbName");
        db.username = dbObj.getString("username");
        db.password = dbObj.getString("password");

        node.database = db;

        return node;
    }
}
