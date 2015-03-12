package server;

import server.handler.RequestHandler;
import server.handler.StatisticHandler;
import server.handler.TrafficHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.traffic.AbstractTrafficShapingHandler;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel sc) throws Exception {
		ChannelPipeline pipeline = sc.pipeline();
		
		pipeline.addLast("decoder", new HttpRequestDecoder());
	    pipeline.addLast("encoder", new HttpResponseEncoder());
	    pipeline.addLast("traffic", new TrafficHandler(
	    		AbstractTrafficShapingHandler.DEFAULT_CHECK_INTERVAL));
		pipeline.addLast("collector", StatisticHandler.getInstance());
		pipeline.addLast("handler", new RequestHandler());
	}
}