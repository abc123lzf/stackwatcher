package com.lzf.stackwatcher.alert.entity;

import java.util.Date;

public class AlertEvent {
    private Long id;

    private Integer alertApplyId;

    private Date time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAlertApplyId() {
        return alertApplyId;
    }

    public void setAlertApplyId(Integer alertApplyId) {
        this.alertApplyId = alertApplyId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}