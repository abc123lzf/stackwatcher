package com.lzf.stackwatcher.alert.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.alert.core.Data;
import com.lzf.stackwatcher.alert.entity.Rule;
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

    @Override
    protected void resolveTimeSerialData(TimeSeriesData tsd, List<Data> out) {
        NovaMemoryMonitorData data = (NovaMemoryMonitorData) tsd;
        String host = data.getHost();
        long time = data.getTime();

        out.add(new Data(host, Rule.Type.NOVA_MEMORY_USED, data.getUsage(), time));
        out.add(new Data(host, Rule.Type.NOVA_MEMORY_USED_UTILIZATION,
                data.getUsage().doubleValue() / data.getSize().doubleValue(), time));
        out.add(new Data(host, Rule.Type.NOVA_SWAPMEMORY_USED, data.getSwapUsage(), time));
        out.add(new Data(host, Rule.Type.NOVA_SWAPMEMORY_USED_UTILIZATION,
                data.getSwapUsage().doubleValue() / data.getSwapSize().doubleValue(), time));
    }
}
