package com.lzf.stackwatcher.alert.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.NovaMemoryMonitorData;

import java.util.List;
import java.util.Properties;

public class NovaMemoryConsumer extends Consumer {

    public NovaMemoryConsumer(String topic, Properties cfg) {
        super(topic, cfg);
    }

    @Override
    protected void handlerJSONData(String json, List<TimeSeriesData> out) throws Exception {
        JSONArray arr = JSON.parseArray(json);
        for(int i = 0; i < arr.size(); i++) {
            NovaMemoryMonitorData data = new NovaMemoryMonitorData();
            JSONObject js = arr.getJSONObject(i);

            data.setSize(js.getLong("size"))
                    .setUsage(js.getLong("used"))
                    .setSwapSize(js.getLong("swapSize"))
                    .setSwapUsage(js.getLong("swapUsed"))
                    .setTime(js.getLong("time"))
                    .setHost(js.getString("host"));

            out.add(data);
        }
    }
}
