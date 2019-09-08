package com.lzf.stackwatcher.common; /**
 * 第十届中国大学生服务外包创新创业大赛
 * 团队：s1mple  选题：A02
 */

/**
 * 
 * @author 李子帆
 * @time 2018年11月19日 下午9:11:21
 */
public class LifecycleException extends RuntimeException {

	private static final long serialVersionUID = 4087418469075268500L;

	public LifecycleException() {
        super();
    }
	
	public LifecycleException(String message) {
        super(message);
    }
	
	public LifecycleException(String message, Throwable cause) {
        super(message, cause);
    }
	
	public LifecycleException(Throwable cause) {
        super(cause);
    }
}
