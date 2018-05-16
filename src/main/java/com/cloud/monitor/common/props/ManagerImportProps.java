package com.cloud.monitor.common.props;

/**
 * @author Gouboting
 * */
public class ManagerImportProps extends PropertiesConfig {
	private static ManagerImportProps instance;
	private ManagerImportProps(){
		instance=this;
	}
	public static ManagerImportProps getInstance(){
		if(instance==null){
			instance=new ManagerImportProps();
		}
		return instance;
	}
	private String classPath;
	private boolean linux;

	@Override
	public void init() {
		super.init();
		classPath=Thread.currentThread().getContextClassLoader().getResource("").getPath();
		LOGGER.info("classPath:"+classPath);
		String os = System.getProperty("os.name");
		LOGGER.info("os:"+os);
		linux=os.toLowerCase().matches("linux");
	}
	public void reloadConfig(){
		super.reload();
	}
	public String getIp() {
		return getProperty("prop.ip", "127.0.0.1");
	}
	
	public String getPort() {
		return getProperty("prop.port", "8080");
	}
	public String getLogPath(){
		return getProperty("prop.log.path", "/tmp");
	}
	/**
	 * 获取classPath路径
	 * @return
	 */
	public String getClassPath(){
		return classPath;
	}
	/**
	 * 是否linux
	 * @return
	 */
	public boolean isLinux(){
		return linux;
	}
}
