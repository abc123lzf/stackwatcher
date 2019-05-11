package com.lzf.stackwatcher.insagent.core;

import org.apache.log4j.Logger;

import com.lzf.stackwatcher.insagent.Container;
import com.lzf.stackwatcher.insagent.Lifecycle;
import com.lzf.stackwatcher.insagent.LifecycleEvent;
import com.lzf.stackwatcher.insagent.LifecycleEventListener;


/**
* @author 李子帆
* @version 1.0
* @date 2018年12月27日 下午5:11:03
* @Description 类说明
*/
public final class LifecycleLoggerListener implements LifecycleEventListener {

	private static final Logger log = Logger.getLogger(LifecycleLoggerListener.class);
	
	public static final LifecycleLoggerListener INSTANCE = new LifecycleLoggerListener();
	
	@Override
	public void lifecycleEvent(LifecycleEvent event) {
		Object source = event.getSource();
		if(source instanceof Container<?>) {
			Container<?> container = (Container<?>) source;
			String name = container.getName();
			switch (event.getType()) {
			case Lifecycle.BEFORE_INIT_EVENT: 
				log.info(String.format("[组件:%s] 正在初始化", name)); break;
			case Lifecycle.AFTER_INIT_EVENT:
				log.info(String.format("[组件:%s] 初始化完成", name)); break;
			case Lifecycle.BEFORE_START_EVENT:
				log.info(String.format("[组件:%s] 正在启动", name)); break;
			case Lifecycle.AFTER_START_EVENT:
				log.info(String.format("[组件:%s] 启动完成，正在运行", name)); break;
			case Lifecycle.BEFORE_RESTART_EVENT:
				log.info(String.format("[组件:%s] 正在重新启动", name)); break;
			case Lifecycle.AFTER_RESTART_EVENT:
				log.info(String.format("[组件:%s] 重新启动完成", name)); break;
			case Lifecycle.BEFORE_STOP_EVENT:
				log.info(String.format("[组件:%s] 正在停止", name)); break;
			case Lifecycle.AFTER_STOP_EVENT:
				log.info(String.format("[组件:%s] 停止完成", name)); break;
			}
		}
	}

	private LifecycleLoggerListener() { }
}