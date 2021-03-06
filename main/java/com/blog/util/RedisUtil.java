package com.blog.util;

/**
 * Created by paul on 2018/5/29.
 */
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.swing.text.StyledEditorKit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<Object, Object> redis;

    @Autowired
    private StringRedisTemplate stringRedis;


    public void redisTemplateSet(Object key, Object value){
        ValueOperations<Object, Object> ops =redis.opsForValue();
        ops.set(key, value);
        redis.expire(key,20, TimeUnit.MINUTES);
    }

    public Object redisTemplateGet(String key){
        ValueOperations<Object, Object> ops =redis.opsForValue();
        return ops.get(key);
    }

    public void redisRemove(String key){
        ValueOperations<Object, Object> ops =redis.opsForValue();
        ops.getOperations().delete(key);
    }

    public void redisTemplateSetForList(Object key, Map<Object,Object> map){
        HashOperations<Object, Object,Object> ops =redis.opsForHash();
        for(Map.Entry<Object, Object> entry : map.entrySet()){
            ops.put(key,entry.getKey(),entry.getValue());
        }
        redis.expire(key,20, TimeUnit.MINUTES);
    }

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
        // 创建一个模板类
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        // 将刚才的redis连接工厂设置到模板类中
        template.setConnectionFactory(factory);
        // 设置key的序列化器
        template.setKeySerializer(new StringRedisSerializer());
        // 设置value的序列化器
        //使用Jackson 2，将对象序列化为JSON
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
     /*   //json转对象类，不设置默认的会将json转成hashmap
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);*/
        template.setValueSerializer(jackson2JsonRedisSerializer);
        //设置hashmap的key,value序列化方式，防止出现乱码
        RedisSerializer stringSerializer = new StringRedisSerializer();
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);
        return template;
    }

}
