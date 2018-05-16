package com.cloud.monitor.common.props;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Gouboting
 * */
public class PropertiesConfig implements Configurable {
	protected final static Logger LOGGER = LoggerFactory.getLogger(PropertiesConfig.class);
	private Properties properties;
	private File configFile;
	private Resource resource;
	private Object lock=new Object();
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	public void init(){
		try {
			configFile = resource.getFile();
			properties = new Properties();
			properties.load(new FileInputStream(configFile));
			LOGGER.info("加载配置文件:"+resource.getFilename()+"成功.");
		} catch (IOException e) {
			//这里需要记录日志
			LOGGER.error("加载配置文件:"+resource.getFilename()+"失败.");
			e.printStackTrace();
		}
	}
	
	public void reload(){
		synchronized (lock) {
			try {
				properties.load(new FileInputStream(configFile));
				LOGGER.info("reload配置文件:"+resource.getFilename()+"成功.");
			}catch (Exception e) {
				LOGGER.error("reload配置文件:"+resource.getFilename()+"失败.");
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getProperty(String key, String defaultVal) {
		String v = getProperty(key);
		if(v==null){
			return defaultVal;
		}
		return v;
	}

	@Override
	public int getProperty(String key, int defaultVal) {
		String v = getProperty(key);
		if(v==null){
			return defaultVal;
		}
		Integer i = Integer.parseInt(v);
		return i.intValue();
	}
	
	public String getProperty(String key){
		if(key==null){
			return null;
		}
		String v = properties.getProperty(key);
		if(v==null||v.isEmpty()){
			return null;
		}
		return v.trim();
	}

}
