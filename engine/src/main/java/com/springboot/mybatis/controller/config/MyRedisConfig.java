package com.springboot.mybatis.controller.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;


@Configuration
public class MyRedisConfig {

    /**
     * 配置RedisTemplate， 个人理解调用RedisTemplate的api时候，这些配置会生效
     * <p>
     * 关于序列化和反序列化，提供了多种策略：
     * 1 JdkSerializationRedisSerializer： 默认为使用的序列化策略；
     * 使用JDK本身序列化机制，将pojo类通过ObjectInputStream/ObjectOutputStream进行序列化操作，最终redis-server中将存储字节序列。是目前默认的序列化策略。
     * <p>
     * 2 StringRedisSerializer：Key或者value为字符串的场景，
     * 根据指定的charset对数据的字节序列编码成string，是“new String(bytes, charset)”和“string.getBytes(charset)”的直接封装。是最轻量级和高效的策略。
     * <p>
     * 3 JacksonJsonRedisSerializer：jackson-json工具提供了javabean与json之间的转换能力，
     * 可以将pojo实例序列化成json格式存储在redis中，也可以将json格式的数据转换成pojo实例。因为jackson工具在序列化和反序列化时，需要明确指定Class类型，因此此策略封装起来稍微复杂。【
     * <p>
     * 4 GenericFastJsonRedisSerializer：另一种javabean与json之间的转换，同时也需要指定Class类型。
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        // 此项必须配置，否则会报java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        template.setValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }


    /**
     * 配置CacheManager，个人理解在使用缓存功能时候这些配置会生效
     * SpringBoot1.x和SpringBoot2.x版本在自定义CacheManager有很大的区别:
     * 在SpringBoot1.x中，RedisCacheManager是可以使用RedisTemplate作为参数注入的
     *
     * @param redisConnectionFactory
     * @return
     * @Bean public CacheManager cacheManager(RedisTemplate redisTemplate) {
     * RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
     * return cacheManager;
     * }
     * 在SpringBoot2.x中，有很大的不同，RedisCacheManager构造器已经无法再使用RedisTemplate进行构造
     */
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {

        //配置序列化策略
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        // 此项必须配置，否则会报java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);


        // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        config = config.entryTtl(Duration.ofMinutes(1))  //entryTtl设置缓存的默认过期时间，也是使用Duration设置;Duration.ofSeconds(600)等价于Duration.ofMinutes(1)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer)) //配置key序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer)) //配置value序列化
                .disableCachingNullValues();//不缓存空值

        // 使用自定义的缓存配置初始化一个cacheManager
        RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .build();
        return cacheManager;
    }
}
