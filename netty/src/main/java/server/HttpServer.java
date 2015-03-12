package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HttpServer implements Runnable {

	private int port = 8080;
    private static final int DEFAULT_BOSS_THREAD;
    private static final int DEFAULT_WORKER_THREAD;

    static {
        DEFAULT_BOSS_THREAD = Runtime.getRuntime().availableProcessors();
        DEFAULT_WORKER_THREAD = DEFAULT_BOSS_THREAD * 2;
    }

    public HttpServer() {
    }

    public HttpServer(int port) {
		this.port = port;
	}

    @Override
	public void run()  {
        Thread.currentThread().setName(getClass().getName());
        EventLoopGroup bossGroup = new NioEventLoopGroup(DEFAULT_BOSS_THREAD);
		EventLoopGroup workerGroup = new NioEventLoopGroup(DEFAULT_WORKER_THREAD);
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap()
				.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ServerInitializer())
				.childOption(ChannelOption.SO_KEEPALIVE, true);

            System.out.println("Server starting listen port:" + port);
            bootstrap.bind(port).sync().channel().closeFuture().sync();
		} catch (InterruptedException e) {
            System.out.println("Server was interrupted");
            System.err.println(e.getMessage());
        } finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}
