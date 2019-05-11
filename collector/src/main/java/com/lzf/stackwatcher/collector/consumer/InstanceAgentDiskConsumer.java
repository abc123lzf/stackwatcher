package com.lzf.stackwatcher.collector.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.InstanceAgentDiskMonitorData;

import java.util.List;
import java.util.Properties;

public class InstanceAgentDiskConsumer extends Consumer {

    public InstanceAgentDiskConsumer(String topic, Properties cfg) {
        super(topic, cfg);
    }

    @Override
    protected void handlerJSONData(String json, List<TimeSeriesData> out) throws Exception {
        JSONArray arr0 = JSON.parseArray(json);
        for(int i = 0; i < arr0.size(); i++) {
            JSONObject object = arr0.getJSONObject(i);

            String uuid = object.getString("uuid");
            long time = object.getLong("time");
            String host = object.getString("host");
            JSONArray arr = object.getJSONArray("devices");

            for (int j = 0; j < arr.size(); j++) {
                InstanceAgentDiskMonitorData data = new InstanceAgentDiskMonitorData();
                JSONObject o = arr.getJSONObject(j);
                data.setHost(host)
                        .setUuid(uuid)
                        .setTime(time);
                data.setDevice(o.getString("device"))
                        .setUsed(o.getLong("used"))
                        .setUtilization(o.getFloat("utilization"))
                        .setFree(o.getLong("free"))
                        .setTotal(o.getLong("total"))
                        .setRdBytes(o.getLong("rdBytes"))
                        .setRdReq(o.getLong("rdReq"))
                        .setWrBytes(o.getLong("wrBytes"))
                        .setWrReq(o.getLong("wrReq"));
                out.add(data);
            }
        }
    }}
