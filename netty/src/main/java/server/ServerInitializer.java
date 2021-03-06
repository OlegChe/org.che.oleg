package server;

import server.handler.GlobalTrafficHandler;
import server.handler.RequestHandler;
import server.handler.StatisticHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel sc) throws Exception {
		ChannelPipeline pipeline = sc.pipeline();
		
		pipeline.addLast("decoder", new HttpRequestDecoder());
	    pipeline.addLast("encoder", new HttpResponseEncoder());
	    pipeline.addLast("traffic", GlobalTrafficHandler.getInstance());
		pipeline.addLast("collector", StatisticHandler.getInstance());
		pipeline.addLast("handler", new RequestHandler());
	}
}