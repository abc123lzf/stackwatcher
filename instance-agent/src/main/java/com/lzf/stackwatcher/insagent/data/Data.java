package com.lzf.stackwatcher.insagent.data;
/**
* @author 李子帆
* @version 1.0
* @date 2018年12月13日 下午8:52:34
* @Description 类说明
*/

import com.alibaba.fastjson.JSONObject;

public abstract strictfp class Data implements java.io.Serializable {
	
	public enum Type {
		CPU("ins.cpudata.detail", (byte)0x01), 
		MEMORY("ins.ram.detail", (byte)0x02),
		NETWORK("ins.network.detail", (byte)0x03), 
		DISK("ins.disk.detail", (byte)0x04);
		
		private final String type;
		
		private final byte typeByte;
		
		private Type(String type, byte typeByte) {
			this.type = type;
			this.typeByte = typeByte;
		}	
		protected String getTypeString() {
			return type;
		}
		
		public byte getTypeByte() {
			return typeByte;
		}
	}

	private static final long serialVersionUID = 292792398628107376L;
	
	
	//虚拟机UUID
	String uuid;
	
	//数据记录时间
	long time;
	
	//数据类型
	final Type type;
	
	protected Data(Type type) {
		super();
		this.time = System.currentTimeMillis();
		this.type = type;
	}
	
	/**
	 * 获取该监控数据的类型
	 * @return
	 */
	public Type type() {
		return type;
	}
	
	/**
	 * 将数据转换成JSON字符串
	 * @return 当前数据对象的JSON表达式
	 */
	public abstract String toJSON();
	
	/**
	 * 向子类提供将Data类基本的数据转换为JSONObject的方法
	 * @return 包含当前虚拟机UUID、数据采集时间、数据类型的JSONObject对象
	 */
	protected final JSONObject baseJSONObject() {
		JSONObject object = new JSONObject();
		object.put("uuid", uuid);
		object.put("time", time);
		object.put("type", type.type);
		return object;
	}
}
