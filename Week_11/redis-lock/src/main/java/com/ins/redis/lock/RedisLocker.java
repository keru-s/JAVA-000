package com.ins.redis.lock;

public interface RedisLocker {
    String lock(String key);

    void unlock(String key, String uuid);

    String updateLockTime(String key, String uuid);
}