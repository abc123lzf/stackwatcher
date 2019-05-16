package com.lzf.stackwatcher.alert.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.alert.core.Data;
import com.lzf.stackwatcher.alert.entity.Rule;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.NovaDiskCapacityMonitorData;

import java.util.List;
import java.util.Properties;

public class NovaDiskCapacityConsumer extends Consumer {

    public NovaDiskCapacityConsumer(String topic, Properties cfg) {
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
                NovaDiskCapacityMonitorData data = new NovaDiskCapacityMonitorData();
                JSONObject o = arr.getJSONObject(j);
                data.setTime(time)
                        .setHost(host);
                data.setDevice(o.getString("device"))
                        .setSize(o.getLong("capacity"))
                        .setUsage(o.getLong("used"));

                out.add(data);
            }
        }
    }

    @Override
    protected void resolveTimeSerialData(TimeSeriesData tsd, List<Data> out) {
        NovaDiskCapacityMonitorData data = (NovaDiskCapacityMonitorData) tsd;
        String host = data.getHost();
        long time = data.getTime();
        String device = data.getDevice();

        out.add(new Data(host, Rule.Type.NOVA_DISK_USED, device, data.getUsage(), time));
        out.add(new Data(host, Rule.Type.NOVA_DISK_USED_UTILIZATION, device,
                (double) data.getUsage() / data.getSize(), time));
    }
}
