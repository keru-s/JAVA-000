package io.kimmking.rpcfx.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import io.kimmking.rpcfx.api.*;
import io.kimmking.rpcfx.exception.RpcfxException;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Rpcfx {

    static {
        // fastjson 中可以根据 type 字段进行反序列化到指定类
        ParserConfig.getGlobalInstance().addAccept("io.kimmking");
    }

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
                    .intercept(InvocationHandlerAdapter.of(new RpcfxInvocationHandler(serviceClass, url, filters)))
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

    public static class RpcfxInvocationHandler implements InvocationHandler {

        public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

        private final Class<?> serviceClass;
        private final String url;
        private final Filter[] filters;

        public <T> RpcfxInvocationHandler(Class<T> serviceClass, String url, Filter... filters) {
            this.serviceClass = serviceClass;
            this.url = url;
            this.filters = filters;
        }

        // 可以尝试，自己去写对象序列化，二进制还是文本的，，，rpcfx是xml自定义序列化、反序列化，json: code.google.com/p/rpcfx
        // int byte char float double long bool
        // [], data class

        @Override
        public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
            RpcfxRequest request = new RpcfxRequest();
            request.setServiceClass(this.serviceClass.getName());
            request.setMethod(method.getName());
            request.setParams(params);

            if (null != filters) {
                for (Filter filter : filters) {
                    if (!filter.filter(request)) {
                        return null;
                    }
                }
            }

            RpcfxResponse response = post(request, url);

            // 这里判断response.status，处理异常
            // 考虑封装一个全局的RpcfxException

            return JSON.parse(response.getResult().toString());
        }

        private RpcfxResponse post(RpcfxRequest req, String url) throws IOException {
            String reqJson = JSON.toJSONString(req);
            System.out.println("req json: " + reqJson);

            // 1.可以复用client
            // 2.尝试使用httpclient或者netty client
            OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(JSONTYPE, reqJson))
                    .build();
            String respJson = client.newCall(request).execute().body().string();
            System.out.println("resp json: " + respJson);
            return JSON.parseObject(respJson, RpcfxResponse.class);
        }
    }
}
