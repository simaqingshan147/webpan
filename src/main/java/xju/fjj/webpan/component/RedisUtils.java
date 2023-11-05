package xju.fjj.webpan.component;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 封装RedisTemplate的工具类
 * @date 2023/10/18 22:12
 */
@Component("redisUtils")
public class RedisUtils<V> {
    @Resource
    private RedisTemplate<String,V> redisTemplate;

    public V get(String key){
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    public Boolean set(String key,V value){
        try {
            redisTemplate.opsForValue().set(key,value);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean set(String key,V value,long time){
        try {
            if(time > 0)
                redisTemplate.opsForValue().set(key,value,time, TimeUnit.SECONDS);
            else
                return set(key,value);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public V getAndDelete(String key) {
        return redisTemplate.opsForValue().getAndDelete(key);
    }
}
