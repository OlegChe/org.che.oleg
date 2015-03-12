package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Description of class.
 * Created: 3/6/2015
 *
 * @author andrey.rodin@playtech.com
 */
public interface CustomHandler {

    HttpResponse handle(ChannelHandlerContext context, HttpRequest request) throws RuntimeException;
}
