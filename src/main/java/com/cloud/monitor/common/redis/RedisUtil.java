package com.cloud.monitor.common.redis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.cloud.monitor.common.MonitorUtil;
import com.cloud.monitor.common.ZDateUtil;
import com.cloud.monitor.common.ZStringUtil;
import com.cloud.monitor.common.ZUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class RedisUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);
	
	@Autowired  
    private JedisPool jedisPool;
	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	private static boolean useRedis = true;
	/**
	 * 是否使用redis
	 */
	public boolean isUseRedis() {
		return useRedis;
	}
    /**
	 * 正常返还链接
	 */
	private void returnResource(Jedis jedis) {
		try {
			jedis.close();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}
	/**
	 * 释放错误链接
	 */
	private void returnBrokenResource(Jedis jedis,String name,Exception msge){
		LOGGER.error(ZDateUtil.dateToString(new Date())+":::::"+name+":::::"+msge.getMessage(), msge);
		if (jedis != null) {
			try {
				jedis.close();
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		}
	}
	/**
	 * 设置缓存生命周期
	 * @param key
	 * @param seconds
	 */
	public void expire(String key,int seconds){
		Jedis jedis = null;
		boolean sucess = true;
		try {
			jedis=jedisPool.getResource();
			jedis.expire(key, seconds);
		} catch (Exception e) {
			sucess = false;
			returnBrokenResource(jedis, "expire:"+key, e);
		} finally {
			if (sucess && jedis != null) {
				returnResource(jedis);
			}
		}
	}
	/**
	 * 将对象保存到hash中,并且设置默认生命周期
	 * @param key
	 * @param object
	 */
	public void setObjectToHash(String key,RedisInterface object){
		setObjectToHash(key, object, RedisKey.NORMAL_LIFECYCLE);
	}
	/**
	 * 将对象保存到hash中,并且设置生命周期
	 * @param key
	 * @param object
	 * @param seconds
	 */
	public boolean setObjectToHash(String key,RedisInterface object,int seconds){
		Jedis jedis = null;
		boolean sucess = true;
		try{
			jedis=jedisPool.getResource();
			Map<String, String> map = object.getAllFeildsToHash();
			jedis.hmset(key, map);
			if(seconds>=0){
				jedis.expire(key, seconds);
			}
		}catch (Exception e) {
			sucess = false;
			returnBrokenResource(jedis, "setObjectToHash:"+key, e);
		}finally{
			if (sucess && jedis != null) {
				returnResource(jedis);
			}
		}
		return sucess;
	}
	/**
	 * 按字段更新缓存里的hash值
	 * @param key
	 * @param map
	 */
	
	public void updateHashMap(String key,Map<String,Object> map){
		updateHashMap(key, map,"uid");
	}
	/**
	 * 更新缓存里的hash值
	 * @param key
	 * @param map
	 * @param unique 此key是由哪个字段拼接而成的
	 */
	
	public void updateHashMap(String key,Map<String,Object> map,String unique){
		Jedis jedis = null;
		boolean sucess = true;
		try{
			map.remove(unique);
			Map<String,String> mapToUpdate=new HashMap<String, String>();
			for(Entry<String, Object> entry:map.entrySet()){
				String temp=entry.getKey();
				Object obj=entry.getValue();
				if(obj instanceof Date){
					mapToUpdate.put(temp, ZDateUtil.dateToString((Date)obj));
				}else{
					mapToUpdate.put(temp, obj.toString());
				}
			}
			jedis=jedisPool.getResource();
			jedis.hmset(key, mapToUpdate);
		}catch (Exception e) {
			sucess = false;
			returnBrokenResource(jedis, "updateHashMap:"+key, e);
		}finally{
			if (sucess && jedis != null) {
				returnResource(jedis);
			}
		}
	}
	/**
	 * 通过反射从缓存里获取一个对象 缺省默认时间，默认的key是有uid这个字段拼接而成
	 * @param <T>
	 * @param key
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObjectFromHash(String key,Class<?> clazz){
		return (T)getObjectFromHash(key, clazz, RedisKey.NORMAL_LIFECYCLE);
	}
	/**
	 * 通过反射从缓存里获取一个对象 缺省默认时间
	 * @param <T>
	 * @param key
	 * @param clazz
	 * @return
	 */
	
	@SuppressWarnings("unchecked")
	public <T> T getObjectFromHash(String key,Class<?> clazz,String uniqueKey){
		return (T)getObjectFromHash(key, clazz, uniqueKey, RedisKey.NORMAL_LIFECYCLE);
	}
	/*
	 * 通过反射从缓存里获取一个对象，默认的key是有uid这个字段拼接而成
	 * @param key
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObjectFromHash(String key,Class<?> clazz, int second){
		return (T)getObjectFromHash(key, clazz, "uid",second);
	}
	/*
	 * 通过反射从缓存里获取一个对象
	 * @param key
	 * @param clazz
	 * @param uniqueKey 此key由哪个字段拼接而成的
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObjectFromHash(String key,Class<?> clazz,String uniqueKey, int seconds){
		Jedis jedis = null;
		boolean sucess = true;
		try {
			jedis=jedisPool.getResource();
			Map<String, String> map=jedis.hgetAll(key);
			if(map.size()>0){
				Object obj = clazz.newInstance();
				if(obj instanceof RedisInterface &&!(obj instanceof RedisListInterface)){
					if(map.size()!=((RedisInterface)obj).getFieldLength()){
						LOGGER.info("+-+ redis getObjectFromHash:"+ clazz.getName() +" expire.hash list size is more than expact. map:"+JSON.toJSONString(map));
						jedis.expire(key, 0);
						return null;
					}
				}
				map.put(uniqueKey, key.split("#")[1]);
				if(seconds>=0){
					jedis.expire(key, seconds);
				}
				return (T)ZUtil.getObjFromMap(map, obj);
			}
		} catch (Exception e) {
			sucess = false;
			returnBrokenResource(jedis, "getObjectFromHash:"+key, e);
		} finally {
			if (sucess && jedis != null) {
				returnResource(jedis);
			}
		}
		return null;
	}
/*
 * 通过反射从缓存里获取一个对象
 * 如果redis map少参数 返回默认值
 * @param key
 * @param clazz
 * @param uniqueKey 此key由哪个字段拼接而成的
 * @return
 */
	@SuppressWarnings("unchecked")
	public <T> T getObjectFromHashWithTrue(String key,Class<?> clazz,String uniqueKey, int seconds){
		Jedis jedis = null;
		boolean sucess = true;
		try {
			jedis=jedisPool.getResource();
			Map<String, String> map=jedis.hgetAll(key);
			if(map.size()>0){
				Object obj = clazz.newInstance();
				map.put(uniqueKey, key.split("#")[1]);
				if(seconds>=0){
					jedis.expire(key, seconds);
				}
				return (T)ZUtil.getObjFromMap(map, obj);
			}
		} catch (Exception e) {
			sucess = false;
			returnBrokenResource(jedis, "getObjectFromHash:"+key, e);
		} finally {
			if (sucess && jedis != null) {
				returnResource(jedis);
			}
		}
		return null;
	}
	/**
	 * 将一个列表对象放入缓存
	 * @param key
	 * @param list
	 */
	
	public void setListToHash(String key,List<RedisListInterface> list){
		setListToHash(key, list, RedisKey.NORMAL_LIFECYCLE);
	}
	/*
	 * 将一个列表对象放入缓存，并设置有效期
	 * @param key
	 * @param list
	 * @param seconds
	 */
	public boolean setListToHash(String key,List<RedisListInterface> list,int seconds){
		Jedis jedis = null;
		boolean sucess = true;
		try {
			//Transaction t = jedis.multi();
			Map<String,String> map=new HashMap<String, String>();
			Map<String,String> keyMap=null;
			String[] keyNames=null;
			for(RedisListInterface po:list){
				keyNames=po.getSubUniqueKey();
				keyMap=ZUtil.getMap(po, keyNames);
				StringBuilder sb=new StringBuilder();
				for(String keyName:keyNames){
					sb.append(keyMap.get(keyName)).append("#");
				}
				map.put(sb.toString(), MonitorUtil.getJsonStr(po.getAllFeildsToHash()));
			}
			//t.hmset(key, map);
			//t.expire(key, seconds);
			//t.exec();
			jedis=jedisPool.getResource();
			jedis.hmset(key, map);
			if(seconds >= 0){
				jedis.expire(key, seconds);
			}
		} catch (Exception e) {
			sucess = false;
			returnBrokenResource(jedis, "setListToHash:"+key, e);
		} finally {
			if (sucess && jedis != null) {
				returnResource(jedis);
			}
		}
		return sucess;
	}
	/**
	 * 从缓存里还原一个列表对象
	 * @param key
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	
	public <T> List<T> getListFromHash(String key,Class<?> clazz,int seconds){
		Jedis jedis = null;
		boolean sucess = true;
		Map<String,String> map=null;
		try {
			jedis=jedisPool.getResource();
			map=jedis.hgetAll(key);
			if(map != null && map.size()>0){
				List<T> rt=new ArrayList<T>();
				RedisListInterface po=null;
				Map<String,String> mapFields=null;
				//String keyNames[]=null;
				for(Entry<String, String> entry:map.entrySet()){
					//String fieldKey = entry.getKey();
					mapFields=MonitorUtil.getMapFromJson(entry.getValue());
					po = (RedisListInterface) clazz.newInstance();
					/*mapFields.put(po.getUniqueKey(), key.split("#")[1]);
					keyNames=po.getSubUniqueKey();
					String uniqueKeys[]=fieldKey.split("#");
					for(int i=0,j=keyNames.length;i<j;i++){
						mapFields.put(keyNames[i], uniqueKeys[i]);
					}*/
					ZUtil.getObjFromMap(mapFields, po);
					rt.add((T)po);
				}
				if(seconds >= 0){
					jedis.expire(key, seconds);
				}
				return rt;
			}
		} catch (Exception e) {
			sucess = false;
			returnBrokenResource(jedis, "getListFromHash:"+key, e);
		} finally {
			if (sucess && jedis != null) {
				returnResource(jedis);
			}
		}
		return null;
	}
	
	/**
	 * 从缓存里还原一个列表对象
	 * @param key
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	
	public <T> T getObjectFromList(String key,String subUnionkey, Class<?> clazz,int seconds){
		Jedis jedis = null;
		boolean sucess = true;
		//Map<String,String> map=null;
		try {
			jedis=jedisPool.getResource();
			String value = jedis.hget(key, subUnionkey);
			if(ZStringUtil.isEmptyStr(value)){
				return null;
			}
			Map<String,String> mapFields = MonitorUtil.getMapFromJson(value);
			RedisListInterface po = (RedisListInterface) clazz.newInstance();
			//mapFields.put(po.getUniqueKey(), key.split("#")[1]);
			/*String[] keyNames=po.getSubUniqueKey();
			String uniqueKeys[]= subUnionkey.split("#");
			for(int i=0,j=keyNames.length;i<j;i++){
				mapFields.put(keyNames[i], uniqueKeys[i]);
			}*/
			ZUtil.getObjFromMap(mapFields, po);
			if(seconds >= 0){
				jedis.expire(key, seconds);
			}
			return (T) po;
		} catch (Exception e) {
			sucess = false;
			returnBrokenResource(jedis, "getListFromHash:"+key, e);
		} finally {
			if (sucess && jedis != null) {
				returnResource(jedis);
			}
		}
		return null;
	}
	
	/**
	 * 批量删除对象
	 * @param key
	 * @param list
	 */
	
	public boolean deleteList(String key,List<RedisListInterface> list){
		Jedis jedis = null;
		boolean sucess = true;
		try {
			String keys[]=new String[list.size()];
			String keyNames[]=null;
			Map<String,String> keyMap=null;
			int index=0;
			for(RedisListInterface po:list){
				keyNames=po.getSubUniqueKey();
				keyMap=ZUtil.getMap(po, keyNames);
				StringBuilder sb=new StringBuilder();
				for(String keyName:keyNames){
					sb.append(keyMap.get(keyName)).append("#");
				}
				keys[index++]=sb.toString();
			}
			jedis=jedisPool.getResource();
			jedis.hdel(key, keys);
		} catch (Exception e) {
			sucess = false;
			returnBrokenResource(jedis, "deleteList:"+key, e);
		} finally {
			if (sucess && jedis != null) {
				returnResource(jedis);
			}
		}
		return sucess;
	}
	
	public void setString(String key,String object){
		setString(key, object, -1);
	}
	public void setString(String key, String value, int seconds) {
		Jedis jedis = null;
		boolean sucess = true;
		try {
			jedis = jedisPool.getResource();
			jedis.set(key, value);
			if(seconds>-1){
				jedis.expire(key, seconds);
			}
		} catch (Exception e) {
			sucess = false;
			returnBrokenResource(jedis, "setString", e);
			expire(key, 0);
		} finally {
			if (sucess && jedis != null) {
				releaseReidsSource(sucess, jedis);
			}
		}
	}
	/**
	 * 设置
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setNxString(String key, String value, int seconds) throws Exception{
		Jedis jedis = null;
		boolean success = true;
		boolean result = false;
		try {
			jedis = jedisPool.getResource();
			result = (jedis.setnx(key, value) != 0);
			if(seconds > -1){
				jedis.expire(key, seconds);
			}
		} catch (Exception e) {
			success = false;
			releasBrokenReidsSource(jedis, key, "setNxString", e, false);
			throw e;
		} finally {
			releaseReidsSource(success, jedis);
		}
		
		return result;
		
	}
	/**
	 * 释放非正常链接
	 * @param jedis
	 * @param key
	 * @param string
	 * @param e
	 */
	private void releasBrokenReidsSource(Jedis jedis, String key, String string, Exception e, boolean deleteKeyFlag){
		returnBrokenResource(jedis, string, e);
		if(deleteKeyFlag){
			expire(key, 0);
		}
	}
	
	/**
	 * 释放成功链接
	 * @param success
	 * @param jedis
	 */
	private void releaseReidsSource(boolean success, Jedis jedis){
		if (success && jedis != null) {
			returnResource(jedis);
		}
	}
	/**
	 * 设置
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setHnxString(String key, String field, String value) throws Exception{
		Jedis jedis = null;
		boolean success = true;
		boolean result = false;
		try {
			jedis = jedisPool.getResource();
			result = (jedis.hsetnx(key, field, value) != 0);
		} catch (Exception e) {
			success = false;
			releasBrokenReidsSource(jedis, key, "setHnxString", e, false);
			throw e;
		} finally {
			releaseReidsSource(success, jedis);
		}
		
		return result;
		
	}
	public long saddString(String key,String value){
		Jedis jedis = null;
		boolean sucess = true;
		try {
			jedis = jedisPool.getResource();
			Long ret = jedis.sadd(key,value);
			return ret == null ? -1 : ret.longValue();
		} catch (Exception e) {
			sucess = false;
			returnBrokenResource(jedis, "saddString"+key, e);
		} finally {
			if (sucess && jedis != null) {
				returnResource(jedis);
			}
		}
		return -1;
	}
	public boolean hdel(String key, String...subKeys) {
		Jedis jedis = null;
		boolean sucess = true;
		try {
			jedis=jedisPool.getResource();
			jedis.hdel(key, subKeys);
		} catch (Exception e) {
			sucess = false;
			returnBrokenResource(jedis, "hdel key:["+key+"],subKeys:["+subKeys+"]", e);
		} finally {
			if (sucess && jedis != null) {
				returnResource(jedis);
			}
		}
		return sucess;
	}
	
}
