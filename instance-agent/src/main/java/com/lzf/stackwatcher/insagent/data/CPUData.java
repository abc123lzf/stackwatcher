package com.lzf.stackwatcher.insagent.data;

import com.alibaba.fastjson.JSONObject;

/**
* @author 李子帆
* @version 1.0
* @date 2018年12月13日 下午8:52:06
* @Description 类说明
*/
public strictfp class CPUData extends Data {
	
	private static final long serialVersionUID = -2417719667369389485L;

	static final String TYPE = "ins.cpudata.detail";
		
	float idle;			//当前空闲CPU百分比
	float system;		//当前内核空间占用CPU百分比
	float iowait;		//当前等待IO操作的CPU百分比
	float user;			//当前用户空间占用CPU百分比
	float other;		//其他占用CPU百分比,
	float totalUsed;	//当前消耗的总CPU百分比

	public CPUData() {
		super(Type.CPU);
	}
	
	public CPUData(double idle, double system, double iowait, double user, 
				   double other, double totalUsed) {
		super(Type.CPU);
		this.idle = (float)idle;
		this.iowait = (float)iowait;
		this.system = (float)system;
		this.user = (float)user;
		this.other = (float)other;
		this.totalUsed = (float)totalUsed;
	}
	
	@Override
	public String toJSON() {
		JSONObject object = baseJSONObject();
		object.put("idle", idle);
		object.put("system", system);
		object.put("iowait", iowait);
		object.put("user", user);
		object.put("other", other);
		object.put("totalused", totalUsed);
		return object.toJSONString();
	}

	public void setIdle(float idle) {
		this.idle = idle;
	}

	public void setSystem(float system) {
		this.system = system;
	}

	public void setIowait(float iowait) {
		this.iowait = iowait;
	}

	public void setUser(float user) {
		this.user = user;
	}

	public void setOther(float other) {
		this.other = other;
	}

	public void setTotalUsed(float totalUsed) {
		this.totalUsed = totalUsed;
	}
}
