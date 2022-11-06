package com.springboot.mybatis.cache;

import com.springboot.mybatis.mapper.annotation.UserMapper2;
import com.springboot.mybatis.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 在实际开发中，对于要反复读写的数据，最好的处理方式是将之在内存中缓存一份，频繁的数据库访问会造成程序效率低下，
 * 同时内存的读写速度本身就要强于硬盘。
 * <p>
 * 当我们首次调用一个缓存方法时，默认会把该方法参数和返回结果作为一个键值对存放在缓存中；
 * 等到下次利用同样的参数来调用该方法时将不再执行该方法，而是直接从缓存中获取结果进行返回。
 *
 *
 * Cache: 缓存接口,定义缓存操作。实现有：RedisCache、EhCacheCache、ConcurrentMapCache等
 *
 * @Cacheable标注在方法上，表示该方法的结果需要被缓存起来， 在缓存时效内再次调用该方法时将不会调用方法本身而是直接从缓存获取结果
 *
 * @CachePut也标注在方法上，和@Cacheable相似也会将方法的返回值缓存起来，不同的是标注@CachePut的方法每次都会被调用，而且每次都会将结果缓存起来，适用于对象的更新，也就是在调用更新方法时候，既执行方法，又将结果进行缓存，这样参能保证缓存数据的正确性
 *
 * CacheEvict  一般标注在删除方法上，也就是在删除数据的同时，也删除缓存
 *
 * keyGenerator:  缓存数据时key生成策略,key和keyGenerator要二选一
 *
 * serialize:   缓存数据时value序列化策略,
 */
//@CacheConfig(cacheNames = "user") 标注在类上，抽取缓存相关注解的公共配置，可抽取的公共配置有缓存名字、主键生成器等,该类中的其他缓存注解就不必再写value或者cacheName了，会使用该名字作为value或cacheName的值，当然也遵循就近原则
@Service
public class CacheService {
    Logger logger = LogManager.getLogger(CacheService.class);

    private UserMapper2 userMapper2;

    @Autowired
    public CacheService(UserMapper2 userMapper2) {
        this.userMapper2 = userMapper2;
    }

    /**
     * cacheNames或者value: 指定缓存的名字(分组名)，相当于是将缓存的键值对进行分组，可以将一个缓存键值对分到多个组里面
     *
     * @return
     */
    @Cacheable(cacheNames = {"user"})
    public List<User> queryAllUSer() {
        logger.debug("查询所有员工");
        List<User> users = userMapper2.queryUserList2();
        return users;
    }

    /**
     * key: 缓存数据可以简单理解成是键值对，key就是指定缓存的键值，默认是使用方法参数的值
     * 对源码调试发现：首次调用的时候缓存的key=1， 缓存的value=User{id=1, name='ocean', password='123'}
     * 源码创建缓存：ConcurrentMapCache.put(Object key, @Nullable Object value)
     *
     * @param id
     * @return
     */
    @Cacheable(cacheNames = {"user"}, key = "#id")//等价于@Cacheable(cacheNames = {"user"})
    public User queryUserByID(int id) {
        logger.debug("按照id查询员工");
        User user = userMapper2.queryUserByID2(id);
        return user;
    }


    /**
     * condition： 指定缓存的条件(满足什么条件时才缓存)，可用SpEL表达式(如#id>0，表示当入参id大于0时才缓存)
     * 方法参数的名字使用方式：
     * 1 #参数名
     * 2 #p0
     * 3 #a0
     * 上面的0代表参数的索引
     *
     * @param id
     * @return
     */
    @Cacheable(cacheNames = {"user"}, condition = "#id>1")
    public User queryUserByIDCacheWithCondition(int id) {
        logger.debug("按照id查询员工,只有当id>1才会进行缓存");
        User user = userMapper2.queryUserByID2(id);
        return user;
    }


    /**
     * unless： 否定缓存，即满足unless指定的条件时，方法的结果不进行缓存，
     * 使用unless时可以在调用的方法获取到结果之后再进行判断
     * 举例：  #result==null，表示返回结果为null时不进行缓存,这里的result应该是关键字
     *
     * @param id
     * @return
     */
    @Cacheable(cacheNames = {"user"}, unless = "#result==null")
    public User queryUserByIDCacheWithUnless(int id) {
        logger.debug("返回结果为null时候不进行缓存");
        User user = userMapper2.queryUserByID2(id);
        return user;
    }

    /**
     * 当数据进行更新的时候，要把变动同步写入到缓存中，这样查询方法在更新之后进行查询的时候，才能获取正确的结果
     *
     * @param user
     * @return
     * @CachePut: 既调用方法，又更新缓存数据，一般用于更新操作，
     * <p>
     * 运行时机：
     * ①先调用目标方法
     * ②将目标方法的结果缓存起来
     * <p>
     * 注意：
     * 1在更新缓存时一定要和想更新的缓存有相同的缓存名称cacheNames以及相同的key
     * 这里的cacheNames="user", key是user的id, 与之前的查询方法保持一致
     * 2 更新缓存时候，缓存的value是方法的返回值，所以更新方法不能是void
     * 3 由于是先运行方法，再进行缓存；又由于返回值是User类型，所以这里的key值可以这样表达：
     * key="result.id"
     */
    @CachePut(cacheNames = {"user"}, key = "#user.id")
    public User updateUser(User user) {
        logger.debug("更新数据库，更新缓存");
        userMapper2.updateUser2(user);
        return user;
    }


    /**
     * @param id
     * @CacheEvict：缓存清除，清除缓存时要指明缓存的名字和key，相当于告诉数据库要删除哪个表中的哪条数据，key默认为参数的值 allEntries：是否清除指定缓存中的所有键值对，默认为false，设置为true时会清除缓存中的所有键值对，与key属性二选一使用
     */
    @CacheEvict(cacheNames = {"user"}, key = "#id")
    public void deleteUserById(int id) {
        logger.debug("删除DB数据，同时删除缓存");
        userMapper2.deleteUser2(id);
    }

    /**
     * 若缓存方法执行过程中出现了异常，”方法调用之后清除缓存“将不起作用
     * 也就是存方法执行过程中出现了异常，缓存将不能被删除
     *
     * @param id
     */
    @CacheEvict(cacheNames = {"user"}, key = "#id")
    public void deleteUserByIdWithOnlyDB(int id) {
        logger.debug("删除DB数据，但是没有删除缓存");
        userMapper2.deleteUser2(id);
        int n = 10 / 0;
    }


    /**
     * beforeInvocation：在@CacheEvict注解的方法调用之前清除指定缓存，默认为false，即在方法调用之后清除缓存，设置为true时则会在方法调用之前清除缓存
     * 在方法调用之前还是之后清除缓存的区别在于方法调用时是否会出现异常，
     * 若不出现异常，这两种设置没有区别，
     * 若出现异常，设置为在方法调用之后清除缓存将不起作用(也就是不能清除缓存)，因为方法调用失败了；
     * 而设置在方法调用之前，无论方法是否出现异常，都会清除缓存
     *
     * @param id
     */
    @CacheEvict(cacheNames = {"user"}, key = "#id", beforeInvocation = true)
    public void deleteUserByIdBeforeInvocation(int id) {
        logger.debug("在删除DB数据之前删除缓存");
        userMapper2.deleteUser2(id);
        int n = 10 / 0;
    }

}
