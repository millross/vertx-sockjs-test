package com.millross.vertx.sockjs;

import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.platform.Verticle;

/**
 * User: jez
 */
public class SockJSVerticle extends Verticle {

    @Override
    public void start() {
        super.start();
        HttpServer httpServer = vertx.createHttpServer();
        SockJSServer server = vertx.createSockJSServer(httpServer);

        server.installApp(new JsonObject().putString("prefix", "/testsocket"), socket -> {
            System.out.println("HELLO");

            socket.dataHandler(buf -> {
                System.out.println(buf.toString() + "*");
                socket.write(new Buffer("Thankyou"));
            });
        });

        httpServer.listen(8080, "0.0.0.0");
    }
}
