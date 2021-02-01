# 学习笔记

## 11周

基于 Redis 封装分布式数据操作

- 在 Java 中实现一个简单的分布式锁；
- 在 Java 中实现一个分布式计数器，模拟减库存。

## 分布式锁

通过 Redis 来实现分布式锁很简单，通过 Redis 提供的原生命令即可实现。

1. 获取锁（原子性操作）

   ```
   SET <锁名> <锁的值> NX PX 30000
   ```

   `NX`：如果不存在

   `PX`：设置锁的超时时间

   `30000`：超时时间，单位毫秒。

   如果设置成功，则会返回 `OK`；当锁未被释放，而其他节点尝试获取锁时，运行该命令，会返回 `(nil)`

2. 释放锁

   通过 lua 脚本来实现原子性，因为处理 lua 脚本的线程是单线程的，不存在其他线程操作而带来干扰，因此天然能保证原子性+事务性。

   ```lua
   if redis.call("get",KEYS[1]) == ARGV[1] then
   	return redis.call("del",KEYS[1])
   else
   	return 0
   end
   ```

   因此，实现一个 Redis 锁，只需要将上述两步用代码实现即可，具体可以参考项目 `redis-lock` 。

### 实现

本次实现使用 Spring Data 中的 RedisTemplate ，具体的 Redis 客户端为 Lettuce。

首先引入依赖：

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- lettuce pool 缓存连接池 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
            <version>2.5.0</version>
        </dependency>
```

注意，这里不仅要引入 `spring-boot-starter-data-redis`，由于使用了 Lettuce，因此也需要引入 commons-pool2 作为 Lettuce 的缓存连接池。



然后进行 Redis 配置

```properties
spring.redis.database=0
spring.redis.host=127.0.0.1
spring.redis.port=6379
spring.redis.password=
spring.redis.timeout=3000
# lettuce setting
spring.redis.lettuce.pool.max-active=8
spring.redis.lettuce.pool.max-wait=-1
spring.redis.lettuce.pool.max-idle=8
spring.redis.lettuce.pool.min-idle=0
```

接着定义 RedisLocker 接口

```java
public interface RedisLocker {
    String lock(String key);

    void unlock(String key, String uuid);
}
```

实现 RedisLocker 接口

```java
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
									// 使用 setIfAbsent 来表示 set NX
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
}

```

测试 RedisLocker 

```java
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
```



## 分布式计数器

