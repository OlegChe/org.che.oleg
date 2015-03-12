package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;

public class TrafficHandler extends ChannelTrafficShapingHandler {
	public TrafficCounter tc = null;
	public TrafficHandler(long checkInterval) {
		super(checkInterval);
	    }
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		tc = trafficCounter();		
		System.out.println("Sent bytes:       " + tc.currentWrittenBytes());
		System.out.println("Write throughput: " + tc.getRealWriteThroughput());
		super.channelRead(ctx, msg);
	}
}
