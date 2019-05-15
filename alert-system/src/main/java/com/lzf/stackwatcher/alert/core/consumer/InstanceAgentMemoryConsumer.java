package com.lzf.stackwatcher.alert.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.InstanceAgentMemoryMonitorData;

import java.util.List;
import java.util.Properties;

public class InstanceAgentMemoryConsumer extends Consumer {

    public InstanceAgentMemoryConsumer(String topic, Properties cfg) {
        super(topic, cfg);
    }

    @Override
    protected void handlerJSONData(String json, List<TimeSeriesData> out) throws Exception {
        JSONArray arr = JSON.parseArray(json);
        for(int i = 0; i < arr.size(); i++) {
            JSONObject object = arr.getJSONObject(i);

            InstanceAgentMemoryMonitorData data = new InstanceAgentMemoryMonitorData();

            data.setUuid(object.getString("uuid"))
                    .setTime(object.getLong("time"))
                    .setHost(object.getString("host"));
            data.setTotal(object.getLong("total"))
                    .setUsed(object.getLong("used"))
                    .setActualUsed(object.getLong("actualused"))
                    .setFree(object.getLong("free"))
                    .setFreeUtilization(object.getLong("freeutilization"))
                    .setUsedUtilization(object.getLong("usedutilization"));

            out.add(data);
        }
    }
}
