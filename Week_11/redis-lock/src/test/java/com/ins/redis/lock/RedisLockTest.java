package com.ins.redis.lock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisLockTest {
    @Autowired
    private RedisLocker locker;

    @Test
    void testLock() {
        String key = "test-lock";
        // first get success
        String uuid = locker.lock(key);
        System.out.println("uuid = " + uuid);
        Assertions.assertNotNull(uuid);
        // second get fail
        String uuid2 = locker.lock(key);
        System.out.println("uuid2 = " + uuid2);
        Assertions.assertNull(uuid2);
        // unlock success
        locker.unlock(key, uuid);
    }
}
