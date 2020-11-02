package io.insight.netty.gateway.inbound;

import io.insight.netty.gateway.filter.HttpRequestAddTokenFilter;
import io.insight.netty.gateway.outbound.HttpClientHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;

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
