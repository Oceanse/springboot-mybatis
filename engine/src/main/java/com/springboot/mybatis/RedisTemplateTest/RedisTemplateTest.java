package com.springboot.mybatis.RedisTemplateTest;


import com.springboot.mybatis.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * 对于redis操作，springboot进行了很好的封装，那就是spring data redis。提供了一个高度封装的RedisTemplate类来进行一系列redis操作
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTemplateTest {

    Logger logger = LogManager.getLogger(RedisTemplateTest.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 测试mysql数据库是否连接成功
     * @throws SQLException
     */
    @Test
    public void mySQLConnection() throws SQLException {
        logger.info("dataSource:{}", dataSource.getClass().getName());

        //spring-boot-starter-jdbc模块默认使用HikariCP作为数据库的连接池,HikariCP的作者是日本人
        Connection connection = dataSource.getConnection();
        logger.info("connection:{}", connection.toString());
    }


    /**
     * 测试redis连接
     * 引入redis的starter之后，会在容器中加入redis相关的一些bean，
     * 其中有两个跟操作redis相关的：RedisTemplate和StringRedisTemplate(用来操作字符串：key和value都是字符串)
     * @throws SQLException
     */
    @Test
    public void redisConnection() throws SQLException {
        stringRedisTemplate.opsForValue().append("name","ocean");
        redisTemplate.opsForValue().set("age","32");
        redisTemplate.opsForValue().set("user",new User(31,"ocean","pwd"));
    }

}