package com.springboot.mybatis.controller.test_mapper.annotation;

import com.springboot.mybatis.mapper.annotation.UserMapper2;
import com.springboot.mybatis.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 这个controller为了简化开发，直接调用dao层
 */
@RestController
public class UserMapperAnnotationController {
    @Autowired
    private UserMapper2 userMapper2;

    @GetMapping("/queryUserList2")
    public List<User> queryUserList() {
        List<User> users = userMapper2.queryUserList2();
        return users;
    }

    @GetMapping("/queryUserByID2")
    public User queryUserByID() {
        User user = userMapper2.queryUserByID2(2);
        return user;
    }

    @GetMapping("/addUser2")
    public String addUser() {
        userMapper2.addUser2(new User(4, "zml", "45632"));
        return "增加用户完毕";
    }

    @GetMapping("/updateUser2")
    public String updateUser() {
        userMapper2.updateUser2(new User(4, "zml", "678910"));
        return "修改用户完毕";
    }

    @GetMapping("/deleteUser2")
    public String deleteUser() {
        userMapper2.deleteUser2(4);
        return "删除用户完毕";
    }
}
