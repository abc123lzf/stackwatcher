package com.lzf.stackwatcher.collector.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.InstanceAgentNetworkMonitorData;

import java.util.List;
import java.util.Properties;

public class InstanceAgentNetworkIOConsumer extends Consumer {

    public InstanceAgentNetworkIOConsumer(String topic, Properties cfg) {
        super(topic, cfg);
    }



    @Override
    protected void handlerJSONData(String json, List<TimeSeriesData> out) throws Exception {
        JSONArray arr = JSON.parseArray(json);
        for(int i = 0; i < arr.size(); i++) {
            JSONObject object = JSON.parseObject(json);

            String uuid = object.getString("uuid");
            long time = object.getLong("time");
            String host = object.getString("host");
            JSONArray arr0 = object.getJSONArray("devices");

            for (int j = 0; j < arr0.size(); j++) {
                InstanceAgentNetworkMonitorData data = new InstanceAgentNetworkMonitorData();
                JSONObject o = arr0.getJSONObject(j);
                data.setUuid(uuid)
                        .setTime(time)
                        .setHost(host);
                data.setDevice(o.getString("device"))
                        .setRxBytes(o.getInteger("netin.rate"))
                        .setRxPackets(o.getInteger("netin.packages"))
                        .setRxErrors(o.getInteger("netin.errors"))
                        .setTxBytes(o.getInteger("netout.rate"))
                        .setTxPackets(o.getInteger("netout.packages"))
                        .setTxErrors(o.getInteger("netout.errors"));
                out.add(data);
            }
        }
    }
}
