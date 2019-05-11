package com.lzf.stackwatcher.insagent.data;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
* @author 李子帆
* @version 1.0
* @date 2018年12月14日 下午4:17:10
* @Description 类说明
*/
public strictfp class DiskData extends Data {
	private static final long serialVersionUID = -1320472108921669768L;
	
	Device[] devices;

	public static final class Device implements java.io.Serializable {
		private static final long serialVersionUID = -697347820314842314L;
		
		String device;		//磁盘设备名
		String dirName;		//挂载路径
		
		long used;			//用量
		float utilization;	//使用量(单位:百分比)
		long free;			//剩余空间
		long total;			//总空间
		
		long rdBytes;		//每秒读入字节数
		long rdReq;			//读入IOPS
		long wrBytes;		//每秒写入字节数
		long wrReq;			//写入IOPS
		
		public Device(String device, String dirName, long used, long free, long total, 
					  long rdBytes, long rdReq, long wrBytes, long wrReq) {
			this.device = device;
			this.dirName = dirName;
			this.used = used;
			this.free = free;
			this.total = total;
			this.rdBytes = rdBytes;
			this.rdReq = rdReq;
			this.wrBytes = wrBytes;
			this.wrReq = wrReq;
			this.utilization = (float)used / total * 100f;
		}
		
		private JSONObject toJSONObject() {
			JSONObject object = new JSONObject();
			object.put("device", device);
			object.put("used", used);
			object.put("utilization", utilization);
			object.put("free", free);
			object.put("total", total);
			object.put("readbytes", rdBytes);
			object.put("readiops", rdReq);
			object.put("writebytes", wrBytes);
			object.put("writeiops", wrReq);
			return object;
		}
	}
	
	public DiskData() {
		super(Type.DISK);
	}
	
	public DiskData(Device[] devices) {
		super(Type.DISK);
		this.devices = devices;
	}
	
	public DiskData(List<Device> list) {
		super(Type.DISK);
		this.devices = list.toArray(new Device[list.size()]);
	}

	@Override
	public String toJSON() {
		JSONObject object = baseJSONObject();
		int len;
		JSONArray arr = new JSONArray(len = devices.length);
		for(int i = 0; i < len; i++) {
			arr.add(devices[i].toJSONObject());
		}
		object.put("devices", arr);
		return object.toJSONString();
	}

}
