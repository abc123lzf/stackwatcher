package com.lzf.stackwatcher.alert.bootstrap;

import com.lzf.stackwatcher.alert.configuration.DruidDataSourceProperties;
import com.lzf.stackwatcher.alert.core.AlertSystem;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(value = {"com.lzf.stackwatcher.alert.dao"})
@EnableConfigurationProperties(value = {DruidDataSourceProperties.class})
@EnableTransactionManagement
@EnableCaching
public class Bootstrap implements CommandLineRunner {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Bootstrap.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }




    @Override
    public void run(String... args) throws Exception {
        AlertSystem alertSystem = new AlertSystem();
    }
}
