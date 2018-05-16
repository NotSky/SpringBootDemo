package com.cloud.monitor.domain;

public class UserVo {

	private int id;
	private String name;
	private String password;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public UserVo() {
	}
	public UserVo(User user){
		id = user.getId();
		name = user.getName();
		password = user.getPassword();
	}
}
