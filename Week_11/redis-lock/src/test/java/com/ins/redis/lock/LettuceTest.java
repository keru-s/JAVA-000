package com.ins.redis.lock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * @author 01387005
 * @since 2021-01-07 19:12
 **/
@SpringBootTest
class LettuceTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void testSet() {
        String key = "hello";
        String value = "world";
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, 2000, TimeUnit.SECONDS);
        String result = redisTemplate.opsForValue().get(key);
        redisTemplate.delete(key);
        System.out.println("result = " + result);
        Assertions.assertEquals(value, result);
    }
}
