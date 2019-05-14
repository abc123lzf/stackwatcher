package com.lzf.stackwatcher.sentinel.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:druid-access.properties")
@ConfigurationProperties(prefix = "druid.access")
public class DruidAccessProperties {

    private String urlMapping;
    private String allowIP;
    private String username;
    private String password;
    private boolean resetEnable;

    public String getUrlMapping() {
        return urlMapping;
    }

    public void setUrlMapping(String urlMapping) {
        this.urlMapping = urlMapping;
    }

    public String getAllowIP() {
        return allowIP;
    }

    public void setAllowIP(String allowIP) {
        this.allowIP = allowIP;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isResetEnable() {
        return resetEnable;
    }

    public void setResetEnable(boolean resetEnable) {
        this.resetEnable = resetEnable;
    }
}
