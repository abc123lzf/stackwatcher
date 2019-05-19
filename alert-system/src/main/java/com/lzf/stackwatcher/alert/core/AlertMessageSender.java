package com.lzf.stackwatcher.alert.core;


import com.lzf.stackwatcher.alert.core.notify.NotifyMethod;
import com.lzf.stackwatcher.alert.dao.AlertMapper;
import com.lzf.stackwatcher.alert.dao.NotifyMapper;
import com.lzf.stackwatcher.alert.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AlertMessageSender {

    private final Map<String, NotifyMethod> notifyMethodMap;

    private final NotifyMapper notifyMapper;

    private final AlertMapper alertMapper;

    @Autowired
    public AlertMessageSender(List<NotifyMethod> notifyMethodList, NotifyMapper notifyMapper, AlertMapper alertMapper) {
        this.notifyMapper = notifyMapper;
        this.alertMapper = alertMapper;

        notifyMethodMap = new ConcurrentHashMap<>();
        for(NotifyMethod m : notifyMethodList) {
            notifyMethodMap.put(m.methodName(), m);
        }
    }

    public void sendMessage(Rule rule, Data data) {
        AlertExample ae = new AlertExample();
        ae.createCriteria().andObjectEqualTo(data.getHost()).andRuleIdEqualTo(rule.getId());
        List<Alert> al = alertMapper.selectByExample(ae);
        if(al.size() == 0)
            return;

        Alert alert = al.get(0);
        String[] idstr = alert.getNotifyIds().split(",");
        List<Integer> ids = new ArrayList<>(idstr.length);

        for(String id : idstr) {
            ids.add(Integer.valueOf(id));
        }

        NotifyExample ne = new NotifyExample();
        ne.createCriteria().andIdIn(ids);
        List<Notify> notifyList = notifyMapper.selectByExample(ne);

        for(Notify notify : notifyList) {
            NotifyMethod m = notifyMethodMap.get(notify.getType().toUpperCase());
            m.send(data, rule, notify);
        }
    }
}
