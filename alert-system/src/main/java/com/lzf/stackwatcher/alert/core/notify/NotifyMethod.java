package com.lzf.stackwatcher.alert.core.notify;

import com.lzf.stackwatcher.alert.core.Data;
import com.lzf.stackwatcher.alert.entity.Notify;
import com.lzf.stackwatcher.alert.entity.Rule;

/**
 * 表示告警信息的通知方式
 */
public interface NotifyMethod {

    /**
     * 通知方式名称，对应数据库notify_inf表中的type列
     * @return 通知方式名称，全部为大写字符
     */
    String methodName();

    /**
     * 发送通知
     * @param data 数据
     * @param rule 告警规则
     * @param notify 通知方式
     */
    void send(Data data, Rule rule, Notify notify);



}
