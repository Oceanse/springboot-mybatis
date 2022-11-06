package com.springboot.mybatis.controller.cache;

import com.springboot.mybatis.cache.CacheService;
import com.springboot.mybatis.cache.CacheService2;
import com.springboot.mybatis.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController()
@RequestMapping("/cache")
public class CacheController {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private CacheService2 cacheService2;

    @GetMapping("/queryAllUser")
    public List<User> fetchaAllUser() {
        List<User> users = cacheService.queryAllUSer();
        return users;
    }

    @GetMapping("/queryUserByID/{id}")
    public User queryUserByID(@PathVariable int id) {
        User user = cacheService.queryUserByID(id);
        return user;
    }

    @GetMapping("/queryUserByIDCacheWithCondition/{id}")
    public User queryUserByIDCacheWithCondition(@PathVariable int id) {
        User user = cacheService.queryUserByIDCacheWithCondition(id);
        return user;
    }


    @GetMapping("/queryUserByIDCacheWithUnless/{id}")
    public User queryUserByIDCacheWithUnless(@PathVariable int id) {
        User user = cacheService.queryUserByIDCacheWithUnless(id);
        return user;
    }


    @GetMapping("/updateUserWithDBAndCache")//@GetMapping方便测试
    public User updateUserWithDBAndCache() {
        User user = new User(4, "king", "mypwd");
        return cacheService.updateUser(user);
    }

    @GetMapping("/deleteUserWithDBAndCache/{id}")//@GetMapping方便测试
    public void deketeUserWithDBAndCache( @PathVariable int id) {
         cacheService.deleteUserById(id);
    }


    @GetMapping("/deleteUserByIdWithOnlyDB/{id}")//@GetMapping方便测试
    public void deleteUserByIdWithOnlyDB( @PathVariable int id) {
        cacheService.deleteUserByIdWithOnlyDB(id);
    }

    @GetMapping("/deleteUserByIdBeforeInvocation/{id}")//@GetMapping方便测试
    public void deleteUserByIdBeforeInvocation( @PathVariable int id) {
        cacheService.deleteUserByIdBeforeInvocation(id);
    }

    @GetMapping("/queryUserByIDuseCaching/{id}")
    public User queryUserByIDuseCaching(@PathVariable int id) {
        User user = cacheService2.queryUserByIDuseCaching(id);
        return user;
    }

    @GetMapping("/queryUserByName/{name}")
    public User queryUserByID(@PathVariable String name) {
        User user = cacheService2.queryUserByName(name);
        return user;
    }


}
