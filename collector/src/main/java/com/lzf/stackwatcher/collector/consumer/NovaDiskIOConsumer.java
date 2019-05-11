package com.lzf.stackwatcher.collector.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.NovaDiskIOMonitorData;

import java.util.List;
import java.util.Properties;

public class NovaDiskIOConsumer extends Consumer {

    public NovaDiskIOConsumer(String topic, Properties cfg) {
        super(topic, cfg);
    }

    @Override
    protected void handlerJSONData(String json, List<TimeSeriesData> out) throws Exception {
        JSONArray arr0 = JSON.parseArray(json);
        for(int i = 0; i < arr0.size(); i++) {
            JSONObject object = arr0.getJSONObject(i);

            String host = object.getString("host");
            JSONArray arr = object.getJSONArray("data");
            long time = object.getLong("time");

            for (int j = 0; j < arr.size(); j++) {
                NovaDiskIOMonitorData data = new NovaDiskIOMonitorData();
                JSONObject o = arr.getJSONObject(j);
                data.setTime(time)
                        .setHost(host);
                data.setDevice(o.getString("device"))
                        .setRdBytes(o.getInteger("reBytes"))
                        .setRdReq(o.getInteger("read"))
                        .setWrBytes(o.getInteger("wrBytes"))
                        .setWrReq(o.getInteger("write"));
                out.add(data);
            }
        }
    }
}
