package com.lzf.stackwatcher.collector.bootstrap;

import com.lzf.stackwatcher.collector.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {

    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        log.info("组建初始化线程启动");
        Collector c = new Collector();
        c.init();
        c.start();
    }

    public Bootstrap() {  }

}
