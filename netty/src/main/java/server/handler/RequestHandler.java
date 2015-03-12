package server.handler;

import server.URL_ADDRESS;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


public class RequestHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private static final String CONTENT_NOT_FOUND = "<html><head></head><center><h3>Requested resource is not available</h3><br><h3>Code: 404. Not found</h3></center>";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
        if (!validateRequest(ctx, msg)) {
            return;
        }
        URL_ADDRESS url = URL_ADDRESS.getByUrl(msg.getUri());
        HttpResponse response;
        if (url == null) {
            response = createErrorResponse();
        } else {
            response = url.getHandler().handle(ctx, msg);
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        System.out.println();
        System.out.println("Get request: " + msg);
        System.out.println();
        System.out.println("Response: " + response);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private boolean validateRequest(ChannelHandlerContext ctx, HttpRequest msg) {
        if (!msg.getDecoderResult().isSuccess()) {
            HttpResponse response = createErrorResponse(HttpResponseStatus.BAD_REQUEST);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return false;
        }
        if (msg.getMethod() != HttpMethod.GET) {
            HttpResponse response = createErrorResponse(HttpResponseStatus.FORBIDDEN);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return false;
        }
        return true;
    }

    private HttpResponse createErrorResponse() {
        DefaultFullHttpResponse value = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_FOUND,
                Unpooled.copiedBuffer(CONTENT_NOT_FOUND, CharsetUtil.US_ASCII));
        return value;
    }

    private HttpResponse createErrorResponse(HttpResponseStatus status) {
        DefaultFullHttpResponse value = new DefaultFullHttpResponse(HTTP_1_1, status,
                Unpooled.copiedBuffer(CONTENT_NOT_FOUND, CharsetUtil.US_ASCII));
        return value;
    }

}
