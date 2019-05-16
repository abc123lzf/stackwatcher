package com.lzf.stackwatcher.alert.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.alert.core.Data;
import com.lzf.stackwatcher.alert.entity.Rule;
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

    @Override
    protected void resolveTimeSerialData(TimeSeriesData tsd, List<Data> out) {
        InstanceAgentMemoryMonitorData data = (InstanceAgentMemoryMonitorData) tsd;
        String uuid = data.getUuid();
        long time = data.getTime();

        out.add(new Data(uuid, Rule.Type.INS_AGENT_MEMORY_USED, data.getUsed(), time));
        out.add(new Data(uuid, Rule.Type.INS_AGENT_MEMORY_ACTUALUSED, data.getActualUsed(), time));
        out.add(new Data(uuid, Rule.Type.INS_AGENT_MEMORY_FREE, data.getFree(), time));
        out.add(new Data(uuid, Rule.Type.INS_AGENT_MEMORY_USED_UTILIZATION, data.getUsedUtilization(), time));
        out.add(new Data(uuid, Rule.Type.INS_AGENT_MEMORY_FREE_UTILIZATION, data.getFreeUtilization(), time));
    }
}
