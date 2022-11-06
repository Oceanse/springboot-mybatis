package com.springboot.mybatis.cache;


import com.springboot.mybatis.mapper.annotation.UserMapper2;
import com.springboot.mybatis.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;


/**
 * @CacheConfig: 标注在类上，抽取缓存相关注解的公共配置，可抽取的公共配置有缓存名字、主键生成器等,
 * 该类中的其他缓存注解就不必再写value或者cacheName了，会使用该名字作为value或cacheName的值;
 * 当然也遵循就近原则
 */
@CacheConfig(cacheNames = "user")
@Service
public class CacheService2 {
    Logger logger = LogManager.getLogger(CacheService.class);

    private UserMapper2 userMapper2;

    @Autowired
    public CacheService2(UserMapper2 userMapper2) {
        this.userMapper2 = userMapper2;
    }


    /**
     * 如果这里不指明cacheNames时候，那么等价于  @Cacheable(cacheNames = {"user"}, key = "#id")
     * @param id
     * @return
     */
    @Cacheable( key = "#id")//等价于@Cacheable(cacheNames = {"user"}, key = "#id")
    public User queryUserByID(int id) {
        logger.debug("按照id查询员工");
        User user = userMapper2.queryUserByID2(id);
        return user;
    }

    /**
     * @Caching是@Cacheable、@CachePut、@CacheEvict的组合，定义复杂的缓存规则
     * 在这个组合中只要有@CachePut就一定会调用被注解的方法
     * 另外，下面注解中的cacheNames可以省略，默认会和全局缓存注解保持一致，也就是是cacheNames默认是user
     * @param id
     * @return
     */
    @Caching(cacheable = {@Cacheable(cacheNames = "user", key = "#id")}, //先查询缓存，没有则创建缓存，然后返回；有则直接返回缓存
            put = {@CachePut(cacheNames = "user", key = "#result.name"), //先执行方法，然后把结果进行缓存;所以这里可以使用result, 缓存的key是User的name，如果有其它按照name查询的缓存方法，就能在缓存中查到数据
                    @CachePut(cacheNames = "user", key = "#result.password")}//先执行方法，然后把结果进行缓存;所以这里可以使用result, 缓存的key是User的password，如果有其它按照password查询的缓存方法，就能在缓存中查到数据
    )
    public User queryUserByIDuseCaching(int id) {
        logger.debug("使用 @Caching组合注解并按照id查询员工");
        User user = userMapper2.queryUserByID2(id);
        return user;
    }


    /**
     * 因为执行queryUserByIDuseCaching时候会在cacheNames={"user"}的缓存中添加以name为key的缓存，
     * 所以这里即使是首次查询也会命中缓存
     * 另外，下面注解中的cacheNames可以省略，默认会和全局缓存注解保持一致，也就是是cacheNames默认是user
     * @param name
     * @return
     */
    @Cacheable(cacheNames = {"user"}, key = "#name")
    public User queryUserByName(String name) {
        logger.debug("按照name查询员工");
        User user = userMapper2.queryUserByName2(name);
        return user;
    }


}
