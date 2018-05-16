package com.cloud.monitor.domain;

import java.util.Map;

import com.cloud.monitor.common.ZUtil;
import com.cloud.monitor.common.redis.RedisListInterface;

/**
 * 资源父类
 * @author GouBoting
 * */
public class ResourcePo implements RedisListInterface{

	private long id;
	private String resource_id;
	
	private static String[] resource_fields= {"id","resource_id"};
	
	@Override
	public Map<String, String> getAllFeildsToHash() {
		return ZUtil.getMap(this, resource_fields);
	}
	@Override
	public String getUniqueKey() {
		return "id";
	}
	@Override
	public int getFieldLength() {
		return 0;
	}
	private static String[] uniqueKey={"id"};
	@Override
	public String[] getSubUniqueKey() {
		return uniqueKey;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getResource_id() {
		return resource_id;
	}
	public void setResource_id(String resource_id) {
		this.resource_id = resource_id;
	}
}
