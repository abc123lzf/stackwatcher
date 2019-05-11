/**
 * 第十届中国大学生服务外包创新创业大赛
 * 团队：s1mple  选题：A02
 */
package com.lzf.stackwatcher.agent;

/**
 * 创建虚拟机资源时抛出的异常
 * @author 李子帆
 * @time 2018年12月12日 上午10:43:45
 */
public class LibvirtCreateException extends Exception {

	private static final long serialVersionUID = 3806098195787849440L;

	public LibvirtCreateException() {
		super();
	}
	
	public LibvirtCreateException(String desc) {
		super(desc);
	}
	
	public LibvirtCreateException(String desc, Throwable t) {
		super(desc, t);
	}
	
	public LibvirtCreateException(Throwable t) {
		super(t);
	}
}
