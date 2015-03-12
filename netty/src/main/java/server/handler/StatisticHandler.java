package server.handler;

import server.StatEntry;
import server.UniqueIPEntry;
import server.utils.URLUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@ChannelHandler.Sharable
public class StatisticHandler extends SimpleChannelInboundHandler<HttpRequest>
		implements CustomHandler {

	private static Map<String, UniqueIPEntry> statIPRequest = new HashMap<>();
	private static Map<String, Integer> statRedirect = new HashMap<>();
	private static List<StatEntry> statConnects = new ArrayList<>();
	private static int counterRequest = 0;
	private static Set<String> uniqueRequests = new HashSet<>();
	private static DefaultChannelGroup channelGroup = new DefaultChannelGroup(
			    "netty-receiver", ImmediateEventExecutor.INSTANCE);
	private static URLUtils urlUtils = new URLUtils();
	private static Lock lock = new ReentrantLock();

	private static final StatisticHandler INSTANCE = new StatisticHandler();
	private static final int STAT_CONNECTS_CAPACITY = 16;
	private static final String URL = "url";

	private StatisticHandler() {
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg)
			throws Exception {
		channelGroup.add(ctx.channel());
		try {
			lock.lock();
			
			setStatIP(ctx, msg);
			setStatRedirect(ctx, msg);
			setStatCommon(ctx, msg);
			setStatConnects(ctx, msg);
			
			ctx.fireChannelRead(msg);
		} finally {
			lock.unlock();
		}
	}
	
	private void setStatConnects(ChannelHandlerContext ctx, HttpRequest msg) {
		statConnects.add(createEntry(ctx, msg, msg.toString().getBytes().length, 0, 0));
		if (statConnects.size() > STAT_CONNECTS_CAPACITY) {
			statConnects.remove(0);
		}
	}

	private void setStatCommon(ChannelHandlerContext ctx, HttpRequest msg) {
		counterRequest++;
		String uri = msg.getUri();
		if (!uniqueRequests.contains(uri)) {
			uniqueRequests.add(uri);
		}
	}

	private void setStatIP(ChannelHandlerContext ctx, HttpRequest msg) {
		String remoteHost = urlUtils.getIP(ctx);
		String uri = msg.getUri();
		Set<String> uniqRequests = null;
		if (!statIPRequest.containsKey(remoteHost)) {
			uniqRequests = new HashSet<>();
			uniqRequests.add(uri);
			statIPRequest.put(remoteHost, createUniqIPEntry(ctx, msg, 1, uniqRequests));
			} else {
				UniqueIPEntry uniqIPEntry = statIPRequest.get(remoteHost);
				Integer reqCount = uniqIPEntry.getReqCount();
				if (!uniqIPEntry.contains(uri)) {
					uniqRequests = uniqIPEntry.getUniqRequests();
					uniqRequests.add(uri);
					statIPRequest.put(remoteHost, createUniqIPEntry(ctx, msg, ++reqCount, uniqRequests));
				} else {
					statIPRequest.put(remoteHost, createUniqIPEntry(ctx, msg, ++reqCount, uniqIPEntry.getUniqRequests()));
				}
			}
	}
	
	private void setStatRedirect(ChannelHandlerContext ctx, HttpRequest msg) {
		if (!msg.getUri().contains("redirect")) {
			return;
		}
		Map<String, List<String>> parameters = new QueryStringDecoder(msg.getUri()).parameters();
        List<String> domain = parameters.get(URL);
        if (domain.size() != 1) {
            return;
        }
       
        String url = domain.get(0);
        if (!urlUtils.validateUrl(url)) {
            return;
        }
        url = urlUtils.parseUrl(url); 
        if (!statRedirect.containsKey(url)) {
        	statRedirect.put(url, 1);
        }
        else{
        	Integer count = statRedirect.get(url);
        	statRedirect.put(url, ++count);
        }
	}
	
	@Override
	public HttpResponse handle(ChannelHandlerContext context,
			HttpRequest request) throws RuntimeException {

		StringBuilder contentStat = new StringBuilder();
		
		// Build table Common stat.
		contentStat.append("<html><head><title>Server statistics</title></head>");
		contentStat.append("<table><tr><td>");

		contentStat.append("<center>Common stat.</center>");
		contentStat.append("<center><table border = 1><tr><th>Requests</th><th>Unique requests</th><th>Connections</th>");
		contentStat.append("<tbody>");
		contentStat.append("<tr><td>" + counterRequest + "</td><td>" + uniqueRequests.size() + "</td><td>" + channelGroup.size() + "</td></tr>");
		contentStat.append("</tbody></table></center>");
		
		contentStat.append("</td><td></td><td></td></tr><tr/><td valign=\"top\">");
		// Build table RRP
		contentStat.append("<center>Recent requests processed</center>");
		contentStat.append("<center><table border = 1><tr><th>IP Address</th><th>URL</th><th>Timestamp</th><th>Sent bytes</th><th>Received bytes</th><th>Speed, bytes/sec</th></tr>");
		contentStat.append("<tbody>");
		for (StatEntry statEntry : statConnects) {
			contentStat.append("<tr><td>" + statEntry.getIp() + "</td><td>" + statEntry.getUrl() + "</td><td>" + DateFormat.getDateTimeInstance().format(statEntry.getTimestamp()) + "</td><td>"+ statEntry.getReceiveByte() + "</td><td>"+ statEntry.getSentByte() + "</td><td>"+ statEntry.getThroughput() + "</td></tr>");
		}
		contentStat.append("</tbody></table></center>");
		
		contentStat.append("</td><td valign=\"top\">");
		
		// Build table IP requests counter 
		contentStat.append("<center>IP requests counter</center>");
		contentStat.append("<center><table border = 1><tr><th>IP Address</th><th>Unique requests</th><th>Requests</th><th>Last request time</th></tr>");
		contentStat.append("<tbody>");
		
		for (UniqueIPEntry uniqIPEntry : statIPRequest.values()) {
			contentStat.append("<tr><td>" + uniqIPEntry.getIp()
					+ "</td><td>" + uniqIPEntry.getUniqReqCount() + "</td><td>" + uniqIPEntry.getReqCount() + "</td><td>" + DateFormat.getDateTimeInstance().format(uniqIPEntry.getLastTimestamp()) + "</td></tr>");
		}
		contentStat.append("</tbody></table></center>");
		contentStat.append("</td><td valign=\"top\">");
		
		// Build table Redirects
		contentStat.append("<center>Redirects</center>");
		contentStat.append("<center><table border = 1><tr><th>URL</th><th>Amount</th></tr>");
		contentStat.append("<tbody>");
		
		for (String url : statRedirect.keySet()) {
			contentStat.append("<tr><td>" + url + "</td><td>" + statRedirect.get(url) + "</td></tr>");
		}
		contentStat.append("</tbody></table></center></td></tr></table></html>");
		
		FullHttpResponse value = new DefaultFullHttpResponse(HTTP_1_1, OK,
				Unpooled.copiedBuffer(contentStat.toString(),
						CharsetUtil.US_ASCII));
		return value;
	}
	
	public StatEntry createEntry(ChannelHandlerContext ctx, HttpRequest msg, Integer receiveBytes, Integer sentBytes, Integer throughput) {
		String remoteHost = urlUtils.getIP(ctx);
		return new StatEntry(msg.getUri(), remoteHost, System.currentTimeMillis(), receiveBytes, sentBytes, throughput);
	}

	private UniqueIPEntry createUniqIPEntry(ChannelHandlerContext ctx,
			HttpRequest msg, Integer reqCount, Set<String> uniqRequest) {
		String remoteHost = urlUtils.getIP(ctx);
		return new UniqueIPEntry(remoteHost, reqCount, System.currentTimeMillis(), uniqRequest);
	}
	
	public static StatisticHandler getInstance() {
		return INSTANCE;
	}
}
