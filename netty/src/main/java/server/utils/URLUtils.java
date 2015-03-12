package server.utils;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class URLUtils {
	private static final String START_TAG = "<";
	private static final String END_TAG = "/>";

	public boolean validateUrl(String url) {
		return url.startsWith(START_TAG) && url.endsWith(END_TAG);
	}

	public String parseUrl(String url) {
		int start = url.indexOf(START_TAG);
		int end = url.indexOf(END_TAG);
		return url.substring(start + 1, end);
	}
	
	public String getIP(ChannelHandlerContext ctx) {
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
	    InetAddress inetaddress = socketAddress.getAddress();
	    String ip = inetaddress.getHostAddress();
		return ip;
	}
}
