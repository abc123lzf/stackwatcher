package com.lzf.stackwatcher.alert.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.InstanceCPUMonitorData;

import java.util.List;
import java.util.Properties;

public class InstanceCPUConsumer extends Consumer {

    public InstanceCPUConsumer(String topic, Properties cfg) {
        super(topic, cfg);
    }

    @Override
    protected void handlerJSONData(String json, List<TimeSeriesData> out) {
        JSONArray arr = JSON.parseArray(json);
        for(int i = 0; i < arr.size(); i++) {
            JSONObject object = arr.getJSONObject(i);

            InstanceCPUMonitorData data = new InstanceCPUMonitorData();

            String uuid = object.getString("uuid");
            Float usage = object.getFloat("usage");
            String host = object.getString("host");
            long time = object.getLong("time");

            data.setUuid(uuid)
                    .setTime(time)
                    .setHost(host);
            data.setUsage(usage);
            out.add(data);
        }
    }


}
