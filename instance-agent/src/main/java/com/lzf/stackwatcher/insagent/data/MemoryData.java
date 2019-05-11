package com.lzf.stackwatcher.insagent.data;

import com.alibaba.fastjson.JSONObject;

/**
* @author 李子帆
* @version 1.0
* @date 2018年12月13日 下午9:20:43
* @Description 类说明
*/
public class MemoryData extends Data {

	private static final long serialVersionUID = 1328747157338910277L;
	
	long total;				//内存总量
	long used;				//已用内存量
	long actualUsed;		//用户实际使用的内存
	long free;				//剩余内存量
	float freeUtilization;	//剩余内存百分比
	float usedUtilization;	//内存使用率
	
	public MemoryData() {
		super(Type.MEMORY);
	}
	
	public MemoryData(long total, long used, long actualUsed, long free) {
		super(Type.MEMORY);
		this.total = total;
		this.used = used;
		this.actualUsed = actualUsed;
		this.free = free;
		this.freeUtilization = (float)free / total * 100.0f;
		this.usedUtilization = (float)used / total * 100.0f;
	}

	@Override
	public String toJSON() {
		JSONObject object = baseJSONObject();
		object.put("total", total);
		object.put("used", used);
		object.put("actualused", actualUsed);
		object.put("free", free);
		object.put("freeutilization", freeUtilization);
		object.put("usedutilization", usedUtilization);
		return object.toJSONString();
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public void setUsed(long used) {
		this.used = used;
	}

	public void setActualUsed(long actualUsed) {
		this.actualUsed = actualUsed;
	}

	public void setFree(long free) {
		this.free = free;
	}

	public void setFreeUtilization(float freeUtilization) {
		this.freeUtilization = freeUtilization;
	}

	public void setUsedUtilization(float usedUtilization) {
		this.usedUtilization = usedUtilization;
	}

	
}
