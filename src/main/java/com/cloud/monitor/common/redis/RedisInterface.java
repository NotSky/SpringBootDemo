package com.cloud.monitor.common.redis;

import java.util.Map;

/**
 * @author GouBoting
 * 获取所有需要保存到缓存里的字段
 * 1、po唯一键值必须为"id"
 * 2、po的所有需要保存的属性必须和数据库里的属性命名一致
 */
public interface RedisInterface {
	public Map<String,String> getAllFeildsToHash();
	public String getUniqueKey();
	public int getFieldLength();
}
