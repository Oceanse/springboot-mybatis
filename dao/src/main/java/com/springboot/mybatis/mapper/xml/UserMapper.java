package com.springboot.mybatis.mapper.xml;

import com.springboot.mybatis.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 基于mapper.xml开发
 */
@Mapper
//@Repository //测试发现可以省略
public interface UserMapper {
    List<User> queryUserList();

    User queryUserByID(int id);

    int addUser(User user);

    int updateUser(User user);

    int deleteUser(int id);
}
