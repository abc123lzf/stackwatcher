package com.lzf.stackwatcher.alert.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.InstanceAgentCPUMonitorData;

import java.util.List;
import java.util.Properties;

public class InstanceAgentCPUConsumer extends Consumer {

    public InstanceAgentCPUConsumer(String topic, Properties cfg) {
        super(topic, cfg);
    }

    @Override
    protected void handlerJSONData(String json, List<TimeSeriesData> out) throws Exception {
        JSONArray arr = JSON.parseArray(json);
        for(int i = 0; i < arr.size(); i++) {
            JSONObject object = arr.getJSONObject(i);

            String uuid = object.getString("uuid");
            long time = object.getLong("time");
            String host = object.getString("host");

            InstanceAgentCPUMonitorData data = new InstanceAgentCPUMonitorData();

            data.setUuid(uuid)
                    .setTime(time)
                    .setHost(host);
            data.setIdle(object.getFloat("idle"))
                    .setSystem(object.getFloat("system"))
                    .setIowait(object.getFloat("iowait"))
                    .setUser(object.getFloat("user"))
                    .setOther(object.getFloat("other"))
                    .setTotalUsed(object.getFloat("totalused"));

            out.add(data);
        }
    }
}
