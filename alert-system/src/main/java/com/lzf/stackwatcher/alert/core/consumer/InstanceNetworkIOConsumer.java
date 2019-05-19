package com.lzf.stackwatcher.alert.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.alert.core.Data;
import com.lzf.stackwatcher.alert.core.WarnRuleChecker;
import com.lzf.stackwatcher.alert.entity.Rule;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.InstanceNetworkIOMonitorData;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class InstanceNetworkIOConsumer extends Consumer {

    private final String publicInterface;

    private final String privateInterface;

    public InstanceNetworkIOConsumer(WarnRuleChecker checker, String topic, Properties cfg) {
        super(checker, topic, cfg);
        publicInterface = Objects.requireNonNull(cfg.getProperty("instance.public-interface-name"));
        privateInterface = Objects.requireNonNull(cfg.getProperty("instance.private-interface-name"));
    }

    @Override
    protected void beforeRun() {
        log.info("Instance-磁盘IO容量监控数据告警处理器启动");
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
    protected void resolveTimeSerialData(TimeSeriesData tsd, List<Data> out) {
        InstanceNetworkIOMonitorData data = (InstanceNetworkIOMonitorData) tsd;
        String uuid = data.getUuid();
        long time = data.getTime();

        if(data.getDevice().equals(publicInterface)) {
            out.add(new Data(uuid, Rule.Type.INS_NET_PUBLIC_IN, "public", data.getRxBytes(), time));
            out.add(new Data(uuid, Rule.Type.INS_NET_PUBLIC_OUT, "public", data.getTxBytes(), time));
            out.add(new Data(uuid, Rule.Type.INS_NET_PUBLIC_IN_PACKAGE, "public", data.getRxPackets(), time));
            out.add(new Data(uuid, Rule.Type.INS_NET_PUBLIC_OUT_PACKAGE, "public", data.getTxPackets(), time));
        } else if(data.getDevice().equals(privateInterface)) {
            out.add(new Data(uuid, Rule.Type.INS_NET_PUBLIC_IN, "private", data.getRxBytes(), time));
            out.add(new Data(uuid, Rule.Type.INS_NET_PUBLIC_OUT, "private", data.getTxBytes(), time));
            out.add(new Data(uuid, Rule.Type.INS_NET_PUBLIC_IN_PACKAGE, "private", data.getRxPackets(), time));
            out.add(new Data(uuid, Rule.Type.INS_NET_PUBLIC_OUT_PACKAGE, "private", data.getTxPackets(), time));
        }
    }
}
