package com.lzf.stackwatcher.insagent.data;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
* @author 李子帆
* @version 1.0
* @date 2018年12月14日 下午2:44:06
* @Description 类说明
*/
public class NetworkData extends Data {
	private static final long serialVersionUID = -530124955997571551L;
	
	Device[] devices;
	
	public static final class Device implements java.io.Serializable {
		private static final long serialVersionUID = 1963016121863962710L;
		
		String deviceName;
		String ipAddr;
		
		int rxBytes;
		int rxPackets;
		int rxErrors;
		
		int txBytes;
		int txPackets;
		int txErrors;
		
		public Device(String deviceName, String ipAddr, int rxBytes, int rxPackets, int rxErrors, 
					  int txBytes, int txPackets, int txErrors) {
			this.rxBytes = rxBytes;
			this.rxPackets = rxPackets;
			this.rxErrors = rxErrors;
			this.txBytes = txBytes;
			this.txPackets = txPackets;
			this.txErrors = txErrors;
		}
		
		private JSONObject toJSONObject() {
			JSONObject object = new JSONObject();
			object.put("device", deviceName);
			object.put("ipaddr", ipAddr);
			object.put("netin.rate", rxBytes);
			object.put("netin.packages", rxPackets);
			object.put("netin.errors", rxErrors);
			object.put("netout.rate", txBytes);
			object.put("netout.packages", txPackets);
			object.put("netout.errors", txErrors);
			return object;
		}
	}

	public NetworkData() {
		super(Type.NETWORK);
	}
	
	public NetworkData(Device[] devices) {
		super(Type.NETWORK);
		this.devices = devices;
	}
	
	public NetworkData(List<Device> list) {
		super(Type.NETWORK);
		this.devices = list.toArray(new Device[list.size()]);
	}
	
	public void addNetworkDeviceData(Device... devices) {
		int oldLen = this.devices.length;
		int newLen = devices.length;
		int totalLen = oldLen + devices.length;
		Device[] narr = new Device[totalLen];
		System.arraycopy(this.devices, 0, narr, 0, oldLen);
		System.arraycopy(devices, 0, narr, oldLen, newLen);
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
