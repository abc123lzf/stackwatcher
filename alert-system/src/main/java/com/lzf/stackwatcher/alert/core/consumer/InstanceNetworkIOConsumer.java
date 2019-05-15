package com.lzf.stackwatcher.alert.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.InstanceNetworkIOMonitorData;

import java.util.List;
import java.util.Properties;

public class InstanceNetworkIOConsumer extends Consumer {

    public InstanceNetworkIOConsumer(String topic, Properties cfg) {
        super(topic, cfg);
    }

    @Override
    protected void handlerJSONData(String json, List<TimeSeriesData> out) throws Exception {
        JSONArray arr0 = JSON.parseArray(json);
        for(int i = 0; i < arr0.size(); i++) {
            JSONObject object = arr0.getJSONObject(i);
            String uuid = object.getString("uuid");
            JSONArray arr = object.getJSONArray("data");
            String host = object.getString("host");
            long time = object.getLong("time");

            for (int j = 0; j < arr.size(); j++) {
                InstanceNetworkIOMonitorData data = new InstanceNetworkIOMonitorData();
                JSONObject o = arr.getJSONObject(j);
                data.setUuid(uuid)
                        .setTime(time)
                        .setHost(host);
                data.setDevice(o.getString("device"))
                        .setDeviceUUID(o.getString("uuid"))
                        .setRxBytes(o.getInteger("rxByteSpeed"))
                        .setRxPackets(o.getInteger("rxPacketSpeed"))
                        .setTxBytes(o.getInteger("txByteSpeed"))
                        .setTxPackets(o.getInteger("txPacketSpeed"));
                out.add(data);
            }
        }
    }

    @Override
    protected void beforeRun() {
        log.info("Instance-data-collector: network-io thread start");
    }
}
