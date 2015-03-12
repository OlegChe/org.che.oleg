package server.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;

import server.utils.URLUtils;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class RedirectHandler implements CustomHandler {
    private static final String CONTENT_NOT_FOUND = "<html><head></head><center><h3>Requested resource is not available</h3><br><h3>Code: 404. Not found</h3></center>";
    private static final String URL = "url";
    private static URLUtils urlUtils = new URLUtils();

    @Override
    public HttpResponse handle(ChannelHandlerContext context, HttpRequest request) throws RuntimeException {
        Map<String, List<String>> parameters = new QueryStringDecoder(request.getUri()).parameters();
        List<String> domain = parameters.get(URL);

        if (domain.size() != 1) {
            return createErrorResponse();
        }
        String url = domain.get(0);

        if (!urlUtils.validateUrl(url)) {
            return createErrorResponse();
        }
        HttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(HttpHeaders.Names.LOCATION, urlUtils.parseUrl(url));
        return response;
    }

    private HttpResponse createErrorResponse() {
        DefaultFullHttpResponse value = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_FOUND,
                Unpooled.copiedBuffer(CONTENT_NOT_FOUND, CharsetUtil.US_ASCII));
        return value;
    }
}
