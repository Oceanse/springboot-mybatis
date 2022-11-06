package com.springboot.mybatis.mapper.annotation;

import com.springboot.mybatis.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于注解开发
 */
@Mapper
public interface UserMapper2 {
    @Select("SELECT * FROM user")
    List<User> queryUserList2();

    @Select("select * from user where id= #{id}")
    User queryUserByID2(int id);

    @Select("select * from user where name= #{name}")
    User queryUserByName2(String name);

    @Insert("insert into user(id,name,password) values (#{id},#{name},#{password})")
    int addUser2(User user);

    @Update("update user set name=#{name},password=#{password} where id=#{id}")
    int updateUser2(User user);

    @Delete("delete from user where id=#{id}")
    int deleteUser2(int id);
}
