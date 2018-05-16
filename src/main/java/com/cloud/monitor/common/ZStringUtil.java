package com.cloud.monitor.common;
/**
 * @author Gouboting
 * */
public class ZStringUtil {
	
	/**默认的文本分隔符 */
	public static final String Default_Split= "#";
	
	/**
	 * 判断字符串是否为空
	 * 
	 * @param string
	 */
	public static boolean isEmptyStr(String string) {
		if (string == null || "".equals(string.trim())) {
			return true;
		}
		return false;
	}
	/**
	 * 自定义格式输出文本{0} {1}
	 * @param s
	 * @param objects
	 */
	public static String format(String s,Object ...objects){
		if(objects!=null&&objects.length>0){
			StringBuilder sb = new StringBuilder();
			int i=0;
			sb.append("{").append(i).append("}");
			int j = s.indexOf(sb.toString());
			while(j>=0){
				if(i<objects.length){
					s=s.replace(sb.toString(), objects[i]==null?"":objects[i].toString());
				}
				i++;
				sb = new StringBuilder();
				sb.append("{").append(i).append("}");
				j = s.indexOf(sb.toString());
			}
		}
		return s;
	}
	
	/**
	 * 获取拼起来的key
	 * @param splitString
	 * @param strings
	 * @return
	 */
	public static String getString(String splitString, String... strings){
		StringBuffer stringBuffer = new StringBuffer();
		for(int i = 0; i < strings.length; i++){
			stringBuffer.append(strings[i]);
			if(i == strings.length - 1){
				break;
			}
			stringBuffer.append(splitString);
		}
		return stringBuffer.toString();
	}
}
