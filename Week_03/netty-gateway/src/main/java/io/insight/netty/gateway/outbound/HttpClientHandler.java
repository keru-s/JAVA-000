package io.insight.netty.gateway.outbound;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.*;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpClientHandler {

    private CloseableHttpAsyncClient httpclient;
    private ExecutorService proxyService;
    private String backendUrl;

    public HttpClientHandler(String backendUrl) {
        this.backendUrl = backendUrl.endsWith("/") ? backendUrl.substring(0, backendUrl.length() - 1) : backendUrl;
        int cores = Runtime.getRuntime().availableProcessors() * 2;
        long keepAliveTime = 1000;
        int queueSize = 2048;
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();//.DiscardPolicy();
        proxyService = new ThreadPoolExecutor(cores, cores,
                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
                new NamedThreadFactory("proxyService"), handler);

        IOReactorConfig ioConfig = IOReactorConfig.custom()
                .setConnectTimeout(1000)
                .setSoTimeout(1000)
                .setIoThreadCount(cores)
                .setRcvBufSize(32 * 1024)
                .build();

        httpclient = HttpAsyncClients.custom().setMaxConnTotal(40)
                .setMaxConnPerRoute(8)
                .setDefaultIOReactorConfig(ioConfig)
                .setKeepAliveStrategy((response, context) -> 6000)
                .build();
        httpclient.start();
    }

    public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx) {
        final String url = this.backendUrl + fullRequest.uri();
        proxyService.submit(() -> fetchGet(fullRequest, ctx, url));
    }

    private void fetchGet(final FullHttpRequest inbound, final ChannelHandlerContext ctx, final String url) {
        final HttpGet httpGet = new HttpGet(url);
        for (Map.Entry<String, String> header : inbound.headers()) {
            httpGet.setHeader(header.getKey(), header.getValue());
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
            System.out.println("request url:" + url + " , response is " + response.content().toString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            exceptionCaught(ctx, e);
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


}

