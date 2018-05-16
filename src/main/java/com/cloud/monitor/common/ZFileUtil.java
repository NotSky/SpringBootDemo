package com.cloud.monitor.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
/**
 * @author Gouboting
 * */
public class ZFileUtil {
	/**
	 * 获取指定路径下的某文件
	 * @param name
	 */
	public static File getFile(String name){
		File file = new File(name);
		if(file==null||!file.isFile()){
			return null;
		}
		return file;
	}
	/**
	 * 按指定格式读取文件(默认utf8编码格式)
	 * @param name
	 */
	public static List<String> getFileStrList(String name){
		return getFileStrList(name, "utf8");
	}
	/**
	 * 获取某一文件的完整内容
	 * @param name
	 * @return
	 */
	public static String getFileStr(String name){
		return getFileStr(name, "utf8");
	}
	/**
	 * 获取某一文件的完整内容
	 * @param name
	 * @param charset
	 * @return
	 */
	public static String getFileStr(String name,String charset){
		StringBuilder sb = new StringBuilder();
		List<String> list = getFileStrList(name, charset);
		if(!ZUtil.isEmpityList(list)){
			for(String s:list){
				sb.append(s).append("\n");
			}
		}
		return sb.toString();
	}
	/**
	 * 按指定格式读取文件 返回字符串列表
	 * @param name
	 * @param charset
	 */
	public static List<String> getFileStrList(String name,String charset){
		File file = getFile(name);
		if(file==null||ZStringUtil.isEmptyStr(charset)){
			return null;
		}
		List<String> rt = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(name),charset));
			String tmp = null;
			while((tmp=br.readLine())!=null){
				rt.add(tmp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br=null;
			}
		}
		return rt;
	}
	/**
	 * 追加消息至文件尾(默认utf8编码)
	 * @param fileName
	 * @param msg
	 */
	public static void appendFileMsg(String fileName,String msg){
		appendFileMsg(fileName, msg, "utf8");
	}
	/**
	 * 追加消息至文件尾 指定输出格式
	 * @param fileName
	 * @param msg
	 * @param charset
	 */
	public static void appendFileMsg(String fileName,String msg,String charset){
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
		File file = getFile(fileName);
		if(file==null){
			File dir = new File(path);
			if(!dir.exists()){
				dir.mkdirs();
			}
			file = new File(fileName);
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		}
		if(file!=null){
			PrintWriter p = null;
			try {
				p = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),charset)));
				p.write(msg);
				p.write("\n");
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(p!=null){
					try {
						p.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					p=null;
				}
			}
		}
	}
	/**
	 * 删除文件[递归]
	 * @param file
	 */
	public static void deleteFile(File file){
		if(file==null){
			return;
		}
		System.out.println("cmd:"+file.getAbsolutePath());
		if(file.isDirectory()){
			if(file.list()!=null){
				for(File f:file.listFiles()){
					deleteFile(f);
				}
			}
		}
		System.out.println("delete:"+file.getAbsolutePath());
		file.delete();
	}
}
