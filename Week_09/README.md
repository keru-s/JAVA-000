# 学习笔记

## 周四-第三题

改造自定义 RPC 的程序，提交到 GitHub：

- 尝试将服务端写死查找接口实现类变成泛型和反射；
- 尝试将客户端动态代理改成字节码生成，添加异常处理；
- 尝试使用 Netty+HTTP 作为 client 端传输方式。

**答案**

### 客户端的动态代理改造成字节码生成

字节码增强有很多种方式，这次选用的是 byte buddy 进行代理，代理代码如下：

```java
private static <T> T createByByteBuddy(Class<T> serviceClass, String url, Filter[] filters) {
    final Class<? extends T> dynamicType = new ByteBuddy()
                        .subclass(serviceClass)
                        .method(ElementMatchers.any())
                        .intercept(InvocationHandlerAdapter.of(
                            new RpcfxInvocationHandler(serviceClass, url, filters)))
                        .make()
                        .load(Rpcfx.class.getClassLoader())
                        .getLoaded();
    return dynamicType.newInstance();
}
```

- `subclass` 指定父类，即被拦截的对象
- `method`指定被拦截的方法，这里使用了通配符 `ElementMatchers.any()`，代表拦截所有的方法
- `intercept`：拦截方法调用，这里将拦截方法的调用委托给了 `RpcfxInvocationHandler` 进行处理，在 `RpcfxInvocationHandler` 中进行了 RPC 调用获取返回结果。
- `make`：生成代理类
- `.load(Rpcfx.class.getClassLoader()).getLoaded()`：将类加载到类加载器中。
- `dynamicType.newInstance()`：创建代理对象的实例。

然后对代理对象的实例进行缓存，减少反复创建

```java
private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();

public static <T> T create(final Class<T> serviceClass, final String url, Filter... filters){
    String className = serviceClass.getName();
    if (CACHE.containsKey(className)) {
    return (T) CACHE.get(className);
    }
    return createByByteBuddy(serviceClass, url, filters);
}

private static <T> T createByByteBuddy(Class<T> serviceClass, String url, Filter[] filters) {
    String className = serviceClass.getName();
    synchronized (CACHE) {
    if (CACHE.containsKey(className)) {
    return (T) CACHE.get(className);
    }
    final Class<? extends T> dynamicType = new ByteBuddy()
                        .subclass(serviceClass)
                        .method(ElementMatchers.any())
                        .intercept(InvocationHandlerAdapter.of(
                        new RpcfxInvocationHandler(serviceClass, url, filters)))
                        .make()
                        .load(Rpcfx.class.getClassLoader())
                        .getLoaded();
    T result = null;
    try {
    	result = dynamicType.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
    	throw new RpcfxException("初始化代理发生异常",e);
    }
        CACHE.putIfAbsent(className, result);
        return result;
    }
}
```



## 周六-第三题

结合 dubbo+hmily，实现一个 TCC 外汇交易处理，代码提交到 GitHub:

- 用户 A 的美元账户和人民币账户都在 A 库，使用 1 美元兑换 7 人民币 ;
- 用户 B 的美元账户和人民币账户都在 B 库，使用 7 人民币兑换 1 美元 ;
- 设计账户表，冻结资产表，实现上述两个本地事务的分布式事务。