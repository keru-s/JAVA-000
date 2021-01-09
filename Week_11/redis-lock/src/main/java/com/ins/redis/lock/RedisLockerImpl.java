package com.ins.redis.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author 01387005
 * @since 2021-01-07 19:26
 **/
@Slf4j
@Service
public class RedisLockerImpl implements RedisLocker {

    private static final String UNLOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then "
            + "return redis.call('del', KEYS[1]) else return 0 end";

    private static final Long ZERO = 0L;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public String lock(String key) {
        String uuid = UUID.randomUUID().toString();

        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, uuid, 5, TimeUnit.MINUTES);
        if (Boolean.TRUE.equals(result)) {
            return uuid;
        }

        return null;
    }

    @Override
    public void unlock(String key, String uuid) {
        if (!StringUtils.hasText(uuid)) {
            return;
        }
        // 指定 lua 脚本，并且指定返回值类型
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
        // 参数一：redisScript，参数二：key列表，参数三：arg（可多个）
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(key), uuid);
        System.out.println("unlock " + (Objects.equals(ZERO, result) ? "failure" : "success"));
    }

    @Override
    public String updateLockTime(String key, String uuid) {
        // todo 等待完成
        return null;
    }
}
