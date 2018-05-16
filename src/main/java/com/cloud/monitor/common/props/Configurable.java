package com.cloud.monitor.common.props;
/**
 * @author Gouboting
 * */
public interface Configurable {
	public String getProperty(String key,String defaultVal);
	public int getProperty(String key,int defaultVal);
}
