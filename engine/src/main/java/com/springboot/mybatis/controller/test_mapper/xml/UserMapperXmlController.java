package com.springboot.mybatis.controller.test_mapper.xml;

import com.springboot.mybatis.mapper.xml.UserMapper;
import com.springboot.mybatis.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 这个controller为了简化开发，直接调用dao层
 */
@RestController
public class UserMapperXmlController {
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/queryUserList")
    public List<User> queryUserList() {
        List<User> users = userMapper.queryUserList();
        return users;
    }

    @GetMapping("/queryUserByID")
    public User queryUserByID() {
        User user = userMapper.queryUserByID(2);
        return user;
    }

    @GetMapping("/addUser")
    public String addUser() {
        userMapper.addUser(new User(4, "zml", "45632"));
        return "增加用户完毕";
    }

    @GetMapping("/updateUser")
    public String updateUser() {
        userMapper.updateUser(new User(4, "zml", "678910"));
        return "修改用户完毕";
    }

    @GetMapping("/deleteUser")
    public String deleteUser() {
        userMapper.deleteUser(4);
        return "删除用户完毕";
    }
}
