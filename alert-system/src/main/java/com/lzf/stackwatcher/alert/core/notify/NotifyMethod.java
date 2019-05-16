package com.lzf.stackwatcher.alert.core.notify;

import com.lzf.stackwatcher.alert.core.Data;
import com.lzf.stackwatcher.alert.entity.Notify;
import com.lzf.stackwatcher.alert.entity.Rule;

public interface NotifyMethod {

    String methodName();

    void send(Data data, Rule rule, Notify notify);

}
