package com.lzf.stackwatcher.alert.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.NovaNetworkIOMonitorData;

import java.util.List;
import java.util.Properties;

public class NovaNetworkIOConsumer extends Consumer {

    public NovaNetworkIOConsumer(String topic, Properties cfg) {
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
                NovaNetworkIOMonitorData data = new NovaNetworkIOMonitorData();
                JSONObject o = arr.getJSONObject(j);
                data.setTime(time)
                        .setHost(host);
                data.setDevice(o.getString("device"))
                        .setRxBytes(o.getLong("rxBytes"))
                        .setRxPackets(o.getLong("rxPackets"))
                        .setRxDrop(o.getLong("rxDrop"))
                        .setTxBytes(o.getLong("txPackets"))
                        .setTxPackets(o.getLong("txPackets"))
                        .setTxDrop(o.getLong("txDrop"))
                        .setRxByteSpeed(o.getInteger("rxByteSpeed"))
                        .setRxPacketSpeed(o.getInteger("rxPacketSpeed"))
                        .setTxByteSpeed(o.getInteger("txByteSpeed"))
                        .setTxPacketSpeed(o.getInteger("txPacketSpeed"));
                out.add(data);
            }
        }
    }
}
