package com.cloud.monitor.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.alibaba.fastjson.JSON;
import com.cloud.monitor.common.props.ManagerImportProps;
/**
 * @author Gouboting
 * */
public class MonitorUtil {
	//private final static Logger LOGGER = LoggerFactory.getLogger(MonitorUtil.class);
	
	/**
	 * 获取json字符串
	 * @param map
	 * @return
	 */
	public static String getJsonStr(Map<String, String> map){
		return JSON.toJSONString(map);
	}
	@SuppressWarnings("unchecked")
	public static Map<String,String> getMapFromJson(String json){
		return JSON.parseObject(json, Map.class);
	}
	/**
	 * 将对象列表 转为Map列表
	 * @param <T>
	 * @param list
	 * @param fields
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<Map<String,Object>> getListMap(List<T> list,String ...fields){
		List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
		if(!isEmpityList(list)){
			for(T t : list){
				Method m;
				try {
					m = t.getClass().getMethod("getMap",fields.getClass());
					Object result = m.invoke(t,(Object)fields);
					mapList.add((Map<String, Object>) result);
				} catch (Exception e) {
				}
			}
		}
		return mapList;
	}
	/**
	 * 判断某个list是否没有数据
	 * @param <T>
	 * @param list
	 * @return
	 */
	public static <T> boolean isEmpityList(List<T> list){
		return ZUtil.isEmpityList(list);
	}
	/**
	 * 将日志对象列表转为String列表
	 * @param <T>
	 * @param list
	 * @return
	 */
	public static <T> String parseListToString(List<T> list){
		StringBuilder sb = new StringBuilder();
		if(!isEmpityList(list)){
			for(T o:list){
				Method m;
				try {
					m = o.getClass().getMethod("toString");
					sb.append(m.invoke(o).toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				sb.append("\n");
			}
		}
		String tmp = sb.toString();
		if(tmp.endsWith("\n")){
			tmp=tmp.substring(0,tmp.lastIndexOf("\n"));
		}
		return tmp;
	}
	public static void logJobUseTime(String msg){
		log("JobUseTime", msg, null);
	}
	/**
	 * 记录一行日志信息
	 * @param fileName
	 * @param msg
	 */
	public static void log(String fileName,String msg,Exception e) {
		String logPath = ManagerImportProps.getInstance().getLogPath();
		String sep = logPath.indexOf("/")!=-1?"/":"\\";
		if(!logPath.endsWith(sep)){
			logPath+=sep;
		}
		Date now = new Date();
		StringBuilder file=new StringBuilder();
		file.append(logPath)
			.append(fileName)
			.append("_")
			.append(ManagerImportProps.getInstance().getIp())
			.append("_")
			.append(ManagerImportProps.getInstance().getPort())
			.append("_")
			.append(ZDateUtil.dateToString(now, "yyyy"))
			.append(ZDateUtil.dateToString(now, "MM"))
			.append(ZDateUtil.dateToString(now, "dd"))
			.append(".txt");
		appendFileMsg(file.toString(), ZDateUtil.dateToString(new Date())+"------"+msg,e);
	}


	/**
	 * 清理列表中的空对象
	 * @param list
	 */
	public static <T> void clearNullElement(List<T> list){
		if(!ZUtil.isEmpityList(list)){
			for(int i=list.size()-1;i>=0;i--){
				if(list.get(i)==null){
					list.remove(i);
				}
			}
		}
	}
	
	public static void printException(Exception e){
		StringBuilder sb = new StringBuilder();
		sb.append(ZDateUtil.dateToString(new Date())).append("\n");
		log("BillingException", sb.toString(), e);
	}
	/**
	 * 追加消息至文件尾(默认utf8编码)
	 * @param fileName
	 * @param msg
	 */
	private static void appendFileMsg(String fileName,String msg,Exception e){
		appendFileMsg(fileName, msg, e, "utf8");
	}
	/**
	 * 追加消息至文件尾 指定输出格式
	 * @param fileName
	 * @param msg
	 * @param charset
	 */
	private static void appendFileMsg(String fileName,String msg,Exception e,String charset){
		if(ZStringUtil.isEmptyStr(fileName)||ZStringUtil.isEmptyStr(msg)||ZStringUtil.isEmptyStr(charset)){
			return;
		}
		String sep = "/";
		if(fileName.indexOf("/")==-1){
			sep = "\\";
		}
		if(fileName.indexOf(sep)==-1){
			return;
		}
		String path = fileName.substring(0, fileName.lastIndexOf(sep)+1);
		String name = fileName.substring(fileName.lastIndexOf(sep)+1);
		if(ZStringUtil.isEmptyStr(path)||ZStringUtil.isEmptyStr(name)){
			return;
		}
		File file = ZFileUtil.getFile(fileName);
		if(file==null){
			File dir = new File(path);
			if(!dir.exists()){
				dir.mkdirs();
			}
			file = new File(fileName);
			try {
				file.createNewFile();
			} catch (IOException e1) {
			}
		}
		if(file!=null){
			PrintWriter p = null;
			try {
				p = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),charset)));
				p.write(msg);
				if(e!=null){
					p.write("\ntrace:\n");
					e.printStackTrace(p);
				}
				p.write("=========================\n");
			} catch (Exception e1) {
				e1.printStackTrace();
			}finally{
				if(p!=null){
					try {
						p.close();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					p=null;
				}
			}
		}
	}
	
	/**
	 * 日期int型(如2014-5-4 表示为140504)
	 * @return
	 */
	public static int getDateTime(){
		int nowTime = 0;
		Calendar now = Calendar.getInstance();
		nowTime += now.get(Calendar.YEAR) % 100 * 10000;
		nowTime += now.get(Calendar.MONTH) * 100;
		nowTime += now.get(Calendar.DATE);
		return nowTime;
	}
	
	/**
	 * 日期int型(如2014-5-4 表示为140504) 
	 * 如果大于今日三点，则为今日三点
	 * 都则为昨日三点
	 * @return
	 */
	public static int getRecordDateTime(){
		int nowTime = 0;
		Calendar now = Calendar.getInstance();
		Date date = new Date();
		now.setTime(date);
		int hour = now.get(Calendar.HOUR_OF_DAY);
		if(hour < 3){
			now.set(Calendar.DATE, now.get(Calendar.DATE) - 1);
		}
		nowTime += now.get(Calendar.YEAR) % 100 * 10000;
		nowTime += now.get(Calendar.MONTH) * 100;
		nowTime += now.get(Calendar.DATE);
		return nowTime;
	}
	
	/**
	 * 检查时否是同一天
	 * @param dateTime
	 * @return
	 */
	public static boolean isOndeDay(int dateTime){
		int currentDateTime = getDateTime();
		return currentDateTime - dateTime >= 1;
	}
	
	public static long getMillisDay(){
		long now = System.currentTimeMillis();
		return now / 86400000L;
	}
	
	/**
	 * 检测字符长度
	 * @param param
	 * @param length
	 * @param emptyFlag TODO
	 * @return
	 */
	public static boolean validStringLength(String param, int length, boolean emptyFlag){
		boolean flag = true;
		if(param == null){
			flag = false;
		}else{
			if(param.length() > length){
				flag = false;
			}
			
			if(param.length() == 0){
				if(!emptyFlag){
					flag = false;
				}
			}
			
		}
	
		return flag;
	}
	
	/**
	 * 获取首个数字
	 * @param keyString
	 * @param split
	 * @return
	 */
	public static int getFirstNumber(String keyString, String split){
		//int result = 0;
		String[] numberStrings = keyString.split(split);
		return Integer.parseInt(numberStrings[0]);
	}
	
	/**
	 * 获取字符串
	 * @param longSet
	 * @return
	 */
	public static String getLongSetToString(Set<Long> longSet){
		StringBuffer sb = new StringBuffer();
		for(Long temp: longSet){
			sb.append(temp);
			sb.append(",");
		}
		return sb.toString();
	}

}
