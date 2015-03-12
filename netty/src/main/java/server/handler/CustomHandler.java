package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public interface CustomHandler {

    HttpResponse handle(ChannelHandlerContext context, HttpRequest request) throws RuntimeException;
}
