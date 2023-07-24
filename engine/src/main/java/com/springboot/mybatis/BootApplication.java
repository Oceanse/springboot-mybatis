package com.springboot.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 配置mapper扫描，有两种方式，一种是直接在UserMapper上面添加@Mapper注解，
 * 缺点就是所有的Mapper都要手动添加，要是落下一个就会报错，
 * 还有一个一劳永逸的办法就是直接在启动类上添加Mapper扫描，
 * @author epanhai
 */
@SpringBootApplication
@EnableCaching //开启基于注解的缓存
//@MapperScan("com.springboot.mybatis.mapper")
public class BootApplication {
    public static void main(String[] args) {

        SpringApplication.run(BootApplication.class,args);
    }
}
