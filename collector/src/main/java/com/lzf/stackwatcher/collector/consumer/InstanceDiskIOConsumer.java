package com.lzf.stackwatcher.collector.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.InstanceDiskIOMonitorData;

import java.util.List;
import java.util.Properties;

public class InstanceDiskIOConsumer extends Consumer {

    public InstanceDiskIOConsumer(String topic, Properties cfg) {
        super(topic, cfg);
    }



    @Override
    protected void handlerJSONData(String json, List<TimeSeriesData> out) throws Exception {
        JSONArray arr0 = JSON.parseArray(json);
        for(int i = 0; i < arr0.size(); i++) {
            JSONObject object = arr0.getJSONObject(i);

            String uuid = object.getString("uuid");
            JSONArray arr = object.getJSONArray("data");
            long time = object.getLong("time");
            String host = object.getString("host");

            for (int j = 0; j < arr.size(); j++) {
                InstanceDiskIOMonitorData data = new InstanceDiskIOMonitorData();
                JSONObject o = arr.getJSONObject(j);
                data.setUuid(uuid)
                        .setTime(time)
                        .setHost(host);
                data.setDevice(o.getString("device"))
                        .setRdBytes(o.getInteger("rdByteSpeed"))
                        .setRdReq(o.getInteger("rdReqSpeed"))
                        .setWrBytes(o.getInteger("wrByteSpeed"))
                        .setWrReq(o.getInteger("wrReqSpeed"));
                out.add(data);
            }
        }
    }
}
