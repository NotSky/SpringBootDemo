package com.cloud.monitor.domain;

import java.util.Date;

public class VMResourcePo extends ResourcePo{

	/**
	 * 用户pin
	 * */
	private String pin;
	/**
	 * 资源状态
	 * */
	private byte status;
	/**
	 * 计费类型
	 * */
	private byte type;
	/**
	 * 区域
	 * */
	private String region;
	/**
	 * 资源创建时间
	 * */
	private Date create_time;
	/**
	 * 监控结果生成时间
	 * */
	private Date monitor_time;
	/**
	 * 资源缺失标识
	 * */
	private byte flag;
	
	public Date getMonitor_time() {
		return monitor_time;
	}
	public void setMonitor_time(Date monitor_time) {
		this.monitor_time = monitor_time;
	}
	public byte getFlag() {
		return flag;
	}
	public void setFlag(byte flag) {
		this.flag = flag;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
}
