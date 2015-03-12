package server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Starter {
	// its starter
    private static HttpServer httpServer;

    public static void main(String[] args) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        int port;
        if (args.length > 0) {
            String arg = args[0];
            if (arg != null && !arg.isEmpty()) {
                port = Integer.parseInt(arg);
                httpServer = new HttpServer(port);
            }
        } else {
            httpServer = new HttpServer();
        }
        service.submit(httpServer);
    }
}
