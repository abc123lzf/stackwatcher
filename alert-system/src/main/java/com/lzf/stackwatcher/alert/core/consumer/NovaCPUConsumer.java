package com.lzf.stackwatcher.alert.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.NovaCPUMonitorData;

import java.util.List;
import java.util.Properties;

public class NovaCPUConsumer extends Consumer {

    public NovaCPUConsumer(String topic, Properties cfg) {
        super(topic, cfg);
    }

    @Override
    protected void handlerJSONData(String json, List<TimeSeriesData> out) throws Exception {
        JSONArray arr = JSON.parseArray(json);
        for(int i = 0; i < arr.size(); i++) {
            NovaCPUMonitorData data = new NovaCPUMonitorData();
            JSONObject object = arr.getJSONObject(i);
            String host = object.getString("host");

            data.setTotal(object.getFloat("total"))
                    .setSystem(object.getFloat("system"))
                    .setUser(object.getFloat("user"))
                    .setIowait(object.getFloat("iowait"))
                    .setOther(object.getFloat("other"))
                    .setTime(object.getLong("time"))
                    .setHost(host);

            out.add(data);
        }
    }
}
