package com.lzf.stackwatcher.sentinel.configuration;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DruidConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DruidConfiguration.class);

    @Autowired private DruidAccessProperties accessProperties;

    @Bean
    public ServletRegistrationBean druidSevlet() {
        logger.info("init Druid Servlet Configuration ");
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), accessProperties.getUrlMapping());
        bean.addInitParameter("allow", accessProperties.getAllowIP());
        bean.addInitParameter("loginUsername", accessProperties.getUsername());
        bean.addInitParameter("loginPassword", accessProperties.getPassword());
        bean.addInitParameter("resetEnable", accessProperties.isResetEnable() ? "true" : "false");
        return bean;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new WebStatFilter());
        bean.addUrlPatterns("/*");
        bean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return bean;
    }
}
