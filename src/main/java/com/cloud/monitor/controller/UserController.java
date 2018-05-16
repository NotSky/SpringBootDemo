package com.cloud.monitor.controller;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.cloud.monitor.domain.User;
import com.cloud.monitor.domain.UserVo;
import com.cloud.monitor.service.UserService;


@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    @GetMapping(value = "/hello")
    public String test() {
    	return "hello spring boot";
    }

    @PostMapping
    public Integer saveUser(@RequestBody User user) {
    	return userService.saveUser(user);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public UserVo findUser(@PathVariable("id") Integer id) {
    	User user = userService.findUserById(id);
    	if(user != null) {
    		return new UserVo(user);
    	}
        return null;
    }
    
    @GetMapping
    public List<User> getAllUsers(){
    	return userService.getAllUsers();
    }
    
    @DeleteMapping(value = "/{id}")
    public Integer deleteUser(@PathVariable("id") Integer id) {
    	return userService.deleteUser(id);
    }
}
