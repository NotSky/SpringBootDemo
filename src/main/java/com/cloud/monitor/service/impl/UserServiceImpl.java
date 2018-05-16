package com.cloud.monitor.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloud.monitor.common.ZUtil;
import com.cloud.monitor.common.redis.RedisKey;
import com.cloud.monitor.common.redis.RedisListInterface;
import com.cloud.monitor.common.redis.RedisUtil;
import com.cloud.monitor.dao.UserDao;
import com.cloud.monitor.domain.User;
import com.cloud.monitor.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserDao userDao;
	@Autowired
	private RedisUtil redisUtil;
	@Override
	public User findUserById(Integer id) {
		User user = null;
		boolean needsReadFromDb = true;
		if(redisUtil.isUseRedis()) {
			user = redisUtil.getObjectFromList(RedisKey.USERPO, id+"#", User.class, RedisKey.NORMAL_LIFECYCLE);
			if(user != null) {
				needsReadFromDb = false;
			}
		}
		if(needsReadFromDb) {
			user = userDao.findUserById(id);
			List<User> list = new ArrayList<>();
			list.add(user);
			sendToCache(list);
		}
		return user;
	}
	@Override
	public Integer saveUser(User user) {
		Integer rt = userDao.saveUser(user);
		List<User> list = new ArrayList<>();
		list.add(user);
		sendToCache(list);
		return rt;
	}
	private void sendToCache(List<User> list){
		if(redisUtil.isUseRedis()){
			if(!ZUtil.isEmpityList(list)){
				List<RedisListInterface> temp=new ArrayList<RedisListInterface>();
				for(User po : list){
					temp.add(po);
				}
				redisUtil.setListToHash(RedisKey.USERPO, temp);
			}
		}
	}
	@Override
	public List<User> getAllUsers() {
		List<User> list = new ArrayList<>();
		boolean needsReadFromDb = true;
		if(redisUtil.isUseRedis()) {
			list = redisUtil.getListFromHash(RedisKey.USERPO, User.class, RedisKey.NORMAL_LIFECYCLE);
			if(list!=null && !list.isEmpty()) {
				needsReadFromDb = false;
			}
		}
		if(needsReadFromDb) {
			list = userDao.getAllUsers();
			sendToCache(list);
		}
		return userDao.getAllUsers();
	}
	@Override
	public Integer deleteUser(Integer id) {
		Integer result = userDao.deleteUser(id);
		redisUtil.hdel(RedisKey.USERPO, id+RedisKey.SPLIT);
		return result;
	}

}
