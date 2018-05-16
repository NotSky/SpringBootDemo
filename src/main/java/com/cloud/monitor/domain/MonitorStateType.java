package com.cloud.monitor.domain;
/**
 * 监控状态类型
 * */
public enum MonitorStateType {
	/**默认正常*/
	NORMAL((byte)0),
	/**IAAS有 计费无*/
	IAAS((byte)1),
	/**IAAS无 计费有*/
	BILLING((byte)2),
	;
	
	private byte value;
	private MonitorStateType(byte v){
		this.value = v;
	}
	
	public byte getValue(){
		return this.value;
	}
	
	public static MonitorStateType parse(byte v){
		for(MonitorStateType type : MonitorStateType.values()){
			if(type.getValue() == v){
				return type;
			}
		}
		return IAAS;
	}
}
