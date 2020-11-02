# 学习笔记

## 编写 Filter 的心得

1. `ChannelInboundHandlerAdapter` 只能加一个，使用两个会使得后面一个 `ChannelInboundHandlerAdapter` 无法正常运行，最终导致网关超时。所以应该是将处理逻辑放在 `ChannelInboundHandlerAdapter` 的继承类中。
2. Filter 中增加了 headers 之后，要在最终的访问层中将 Headers 的内容一并传递过去，否则后端还是无法接收到对应 Headers 中设置的 k-v。



## 第二题

整合上次作业的 HttpClinet

### 解答

```java
 public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx) {
        final String url = this.backendUrl + fullRequest.uri();
        proxyService.submit(()->fetchGet(fullRequest, ctx, url));
    }

private void fetchGet(final FullHttpRequest inbound, final ChannelHandlerContext ctx, final String url) {
    final HttpGet httpGet = new HttpGet(url);
    // 将 inbound 中所有请求头的内容设置到 httpGet 中
    for (Map.Entry<String, String> header : inbound.headers()) {
        httpGet.setHeader(header.getKey(),header.getValue());
    }
    httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);

    FullHttpResponse response = null;
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
        String result = httpClient.execute(httpGet, httpResponse -> {
            int status = httpResponse.getStatusLine().getStatusCode();
            if (status < 200 || status >= 300) {
                return "request error";
            }
            HttpEntity entity = httpResponse.getEntity();
            return entity != null ? EntityUtils.toString(entity) : "";
        });
        response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(result.getBytes()));
        response.headers().set("Content-Type", "application/json");
        response.headers().setInt("Content-Length", result.length());

    } catch (IOException e) {
        response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
        exceptionCaught(ctx,e);
    } finally {
        if (!HttpUtil.isKeepAlive(inbound)) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.write(response);
        }
        ctx.flush();
    }
}

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
```



## 第三题

实现过滤器。

### 解答

过滤器实现如下：该过滤器作用非常简单，往  header 中设置一个固定的 k-v。

```java
public class HttpRequestAddTokenFilter implements HttpRequestFilter {
    @Override
    public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
        fullRequest.headers().set("name","insight");
    }
}
```

然后将其作为 `HttpInboundHandler` 的成员变量，调用 `filter`

```java
public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

    private final String proxyServer;
    private HttpClientHandler handler;
    private HttpRequestAddTokenFilter filter;

    public HttpInboundHandler(String proxyServer) {
        this.proxyServer = proxyServer;
        handler = new HttpClientHandler(this.proxyServer);
        filter = new HttpRequestAddTokenFilter();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            FullHttpRequest fullRequest = (FullHttpRequest) msg;
            filter.filter(fullRequest,ctx);
            handler.handle(fullRequest, ctx);

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

}
```



然后用上节课提供的 netty-server 项目做改造，搭建一个后端服务，改写 handler 中的响应部分，获取请求头中的 `name`，并作为响应返回。

```java
public class HttpHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(HttpHandler.class);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            FullHttpRequest fullRequest = (FullHttpRequest) msg;
            String uri = fullRequest.uri();
            logger.info("接收到的请求url为{}", uri);
            handlerRequest(fullRequest, ctx);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void handlerRequest(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
        FullHttpResponse response = null;
        try {
            // 读取响应头中的 name
            String name = fullRequest.headers().get("name");
            String value = "hello,"+name;
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(value.getBytes(StandardCharsets.UTF_8)));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", response.content().readableBytes());

        } catch (Exception e) {
            logger.error("处理测试接口出错", e);
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
        } finally {
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(response);
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
```

结果为：

```
NIOGateway 1.0.0 starting...
NIOGateway 1.0.0 started at http://localhost:8888 for server:http://localhost:8808
request url:http://localhost:8808/ , response is hello,insight
```

