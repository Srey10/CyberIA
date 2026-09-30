import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class App {

    static final Logger log = LogManager.getLogger(App.class);

    public static void main(String[] args) throws Exception {

        HttpServer server =
                HttpServer.create(new InetSocketAddress("127.0.0.1", 8081), 0);

        server.createContext("/", ex -> {

            String name = "guest";
            String q = ex.getRequestURI().getRawQuery();

            if (q != null) {
                for (String p : q.split("&")) {
                    if (p.startsWith("name=")) {
                        name = URLDecoder.decode(
                                p.substring(5),
                                StandardCharsets.UTF_8
                        );
                    }
                }
            }

            log.info("Request from " + ex.getRemoteAddress()
                    + " name=" + name);

            log.info("UA="
                    + ex.getRequestHeaders().getFirst("User-Agent"));

            byte[] body =
                    ("Hello, " + name + "\n")
                    .getBytes(StandardCharsets.UTF_8);

            ex.sendResponseHeaders(200, body.length);

            try (OutputStream os = ex.getResponseBody()) {
                os.write(body);
            }
        });

        server.start();

        System.out.println(
                "Running on http://127.0.0.1:8081"
        );
    }
}