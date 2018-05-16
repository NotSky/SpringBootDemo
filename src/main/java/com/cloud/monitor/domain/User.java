package com.cloud.monitor.domain;

import java.io.Serializable;
import java.util.Map;

import com.cloud.monitor.common.ZUtil;
import com.cloud.monitor.common.redis.RedisListInterface;


/**
 * 用户实体类
 *
 */
public class User implements RedisListInterface,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String[] fileds= {"id","name","password"};
	
	
	@Override
	public Map<String, String> getAllFeildsToHash() {
		return ZUtil.getMap(this, fileds);
	}

	@Override
	public String getUniqueKey() {
		return "id";
	}

	@Override
	public int getFieldLength() {
		return fileds.length;
	}
	private String[] uniqueKey = {"id"};
	@Override
	public String[] getSubUniqueKey() {
		return uniqueKey;
	}
	
    /**
     * 用户编号
     */
	protected Integer id;

    /**
     * 用户名称
     */
	protected String name;
    
    /**
     * 密码
     * */
	protected String password;

	public User() {
	}

	public User(Integer id, String name, String pwd) {
		super();
		this.id = id;
		this.name = name;
		this.password = pwd;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("User{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(",password='").append(password).append('\'').append("}");
		return sb.toString();
	}

}
