package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GlobalTrafficHandler extends GlobalChannelTrafficShapingHandler {
    private static final GlobalTrafficHandler INSTANCE = new GlobalTrafficHandler(Executors.newSingleThreadScheduledExecutor(), 100L);
    private static Lock lock = new ReentrantLock();
    private static final StatisticHandler stat = StatisticHandler.getInstance();
   
    private GlobalTrafficHandler(ScheduledExecutorService executor, long checkInterval) {
        super(executor, checkInterval);
    }
    @Override
    protected long calculateSize(Object msg) {
        long size = super.calculateSize(msg);
        if (size < 0) {
            return msg.toString().getBytes().length;
        }
        return size;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        if (msg instanceof LastHttpContent) {
            return;
        }
        try {
        	lock.lock();
        	stat.setStatConnectsTraffic(trafficCounter().currentReadBytes(), trafficCounter().currentWrittenBytes(), trafficCounter().lastWriteThroughput());
            System.out.println(trafficCounter());
        } finally {
        	lock.unlock();
        }
        
    }
    public static GlobalTrafficHandler getInstance() {
        return INSTANCE;
    }
}

