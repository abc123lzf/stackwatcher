package com.lzf.stackwatcher.agent.core;

import org.apache.log4j.Logger;

import java.util.concurrent.ThreadPoolExecutor;
/**
 * 定时任务拒绝执行处理器
 * 默认的策略为记录日志然后将线程池任务队列头部的任务删除
 */
class DiscardOldestPolicyWithLogger extends ThreadPoolExecutor.DiscardOldestPolicy {

    private final Logger log;

    DiscardOldestPolicyWithLogger(Class<?> klass){
        log = Logger.getLogger(klass);
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        log.warn("LibvirtService: 有一个Runnable因为任务队列过长而被丢弃");
        super.rejectedExecution(r, e);
    }
}
