package com.ins.redis.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author 01387005
 * @since 2021-01-07 19:26
 **/
@Service
public class RedisLockerImpl implements RedisLocker {

    private static final String UNLOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then "
            + "return redis.call('del', KEYS[1]) else return 0 end";

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String lock(String key) {
        return null;
    }

    @Override
    public void unlock(String key, String uuid) {
        if (!StringUtils.hasText(uuid)) {
            return;
        }
    //     TODO 执行脚本释放
    }

    @Override
    public String updateLockTime(String key, String uuid) {
        return null;
    }
}
