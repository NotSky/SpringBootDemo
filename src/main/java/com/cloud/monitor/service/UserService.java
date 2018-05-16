package com.cloud.monitor.service;

import java.util.List;

import com.cloud.monitor.domain.User;


public interface UserService {

	User findUserById(Integer id);

	Integer saveUser(User user);

	List<User> getAllUsers();

	Integer deleteUser(Integer id);
}
