package xju.fjj.webpan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: redisTemplate配置类
 * @date 2023/10/19 22:43
 */
@Configuration
public class RedisConfig<V>{
    @Bean
    public RedisTemplate<String,V> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,V> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        //设置key序列化方式
        redisTemplate.setKeySerializer(RedisSerializer.string());
        //设置value序列化方式
        redisTemplate.setValueSerializer(RedisSerializer.json());
        //设置hash的key序列化方式
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        //设置hash的value的序列化方式
        redisTemplate.setHashValueSerializer(RedisSerializer.json());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
