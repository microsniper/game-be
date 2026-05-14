package com.sniper.game.wordgame.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 *
 * @author sniper
 * @since 1.0
 */
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存
     *
     * @param key   键
     * @param value 值
     * @param expire 过期时间
     * @param unit  时间单位
     */
    public void set(String key, Object value, long expire, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, expire, unit);
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     *
     * @param key 键
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 设置过期时间
     *
     * @param key    键
     * @param expire 过期时间
     * @param unit   时间单位
     * @return 是否成功
     */
    public boolean expire(String key, long expire, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, expire, unit));
    }

    /**
     * 如果不存在则设置（用于分布式锁/幂等性）
     *
     * @param key    键
     * @param value  值
     * @param expire 过期时间
     * @param unit   时间单位
     * @return 是否设置成功（false表示key已存在）
     */
    public boolean setIfAbsent(String key, Object value, long expire, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, expire, unit));
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 递增因子
     * @return 递增后的值
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }
}
