package com.lzf.stackwatcher.agent;
/**
* @author 李子帆
* @version 1.0
* @date 2018年12月12日 下午7:35:34
* @Description 对虚拟机进行操作时抛出的检查异常
*/
public class DomainException extends Exception {

	private static final long serialVersionUID = -5419861866079195788L;
	
	public static final int IS_RUNNING = 1;
	public static final int IS_SHUTDOWN = 2;
	public static final int NOT_FOUND = 3;
	public static final int LIBVIRT_ERROR = -1;
	
	//错误代码
	private final int errorCode;

	public DomainException(int errorCode) {
		super();
		this.errorCode = errorCode;
	}
	
	public DomainException(int errorCode, String desc) {
		super(desc);
		this.errorCode = errorCode;
	}
	
	public DomainException(int errorCode, String desc, Throwable t) {
		super(desc, t);
		this.errorCode = errorCode;
	}
	
	public DomainException(int errorCode, Throwable t) {
		super(t);
		this.errorCode = errorCode;
	}

	public int errorCode() {
		return errorCode;
	}
}
