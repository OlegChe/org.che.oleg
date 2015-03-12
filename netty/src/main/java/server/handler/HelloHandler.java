package server.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


public class HelloHandler implements CustomHandler {

    private static final int DELAY = 10;
    private static final String CONTENT_HELLO = "<html><head></head><center><h3>Hello, world!</h3></center>";

    @Override
    public HttpResponse handle(ChannelHandlerContext context, HttpRequest request) throws RuntimeException {
        FullHttpResponse value = new DefaultFullHttpResponse(HTTP_1_1, OK,
                Unpooled.copiedBuffer(CONTENT_HELLO, CharsetUtil.US_ASCII));
        try {
            Thread.sleep(DELAY * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return value;
    }
}
