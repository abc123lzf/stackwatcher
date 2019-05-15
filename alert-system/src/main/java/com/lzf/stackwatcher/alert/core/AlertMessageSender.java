package com.lzf.stackwatcher.alert.core;

import com.lzf.stackwatcher.alert.entity.Notify;
import com.lzf.stackwatcher.alert.entity.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlertMessageSender {

    @Autowired private RedisTemplate<String, Number> siRedis;

    public void receive(Rule rule, List<Notify> list) {
        for(Notify notify : list) {

        }
    }
}
