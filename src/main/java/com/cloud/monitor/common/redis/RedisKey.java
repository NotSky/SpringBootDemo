package com.cloud.monitor.common.redis;

public interface RedisKey {

	/**正常缓存有效时间24h*/
	public static final int NORMAL_LIFECYCLE=86400;

	/**正常一个月缓存有效时间*/
	public static final int NORMAL_MONTH_LIFECYCLE=NORMAL_LIFECYCLE*36;
	
	public static final String SPLIT = "#";
	
	public static final String USERPO = "user#";
}
