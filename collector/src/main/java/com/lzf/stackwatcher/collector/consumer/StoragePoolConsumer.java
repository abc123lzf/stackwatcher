package com.lzf.stackwatcher.collector.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.NovaStoragePoolData;

import java.util.List;
import java.util.Properties;

public class StoragePoolConsumer extends Consumer {

    public StoragePoolConsumer(String topic, Properties cfg) {
        super(topic, cfg);
    }

    @Override
    protected void handlerJSONData(String json, List<TimeSeriesData> out) throws Exception {
        JSONArray arr = JSON.parseArray(json);
        for(int i = 0; i < arr.size(); i++) {
            NovaStoragePoolData data = new NovaStoragePoolData();
            JSONObject obj = arr.getJSONObject(i);

            data.setHost(obj.getString("host"))
                .setTime(obj.getLongValue("time"));

            data.setName(obj.getString("name"))
                .setUuid(obj.getString("uuid"))
                .setAllocation(obj.getLongValue("allocation"))
                .setCapacity(obj.getLongValue("capacity"));

            out.add(data);
        }
    }
}
