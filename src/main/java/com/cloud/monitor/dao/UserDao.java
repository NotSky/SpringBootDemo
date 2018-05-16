package com.cloud.monitor.dao;



import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.cloud.monitor.domain.User;
@Mapper
public interface UserDao {
	@Select("SELECT * FROM user where id = #{id}")
	@Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "password", column = "password"),
	})
    User findUserById(@Param("id") Integer userId);

	@Insert("insert into user(id,name,password) values(#{id},#{name},#{password})")
	@Options(keyProperty="id",keyColumn="id",useGeneratedKeys=true)  
	@Results({@Result(property = "id",column = "id")})
	Integer saveUser(User user);

	@Select("select * from user")
	List<User> getAllUsers();

	@Delete("delete from user where id = #{id}")
	Integer deleteUser(Integer id);
}
