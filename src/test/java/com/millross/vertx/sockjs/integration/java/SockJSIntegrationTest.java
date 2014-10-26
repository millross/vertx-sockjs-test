package com.millross.vertx.sockjs.integration.java;

import com.millross.vertx.sockjs.SockJSVerticle;
import org.junit.Test;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.vertx.testtools.TestVerticle;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.vertx.testtools.VertxAssert.*;

/**
 * User: jez
 */
public class SockJSIntegrationTest extends TestVerticle{

    @Test
    public void testSockJSWithClient() throws Exception {
        List<Transport> transports = new ArrayList<>();
        JettyWebSocketClient webSocketClient = new JettyWebSocketClient();
        webSocketClient.start();
        transports.add(new WebSocketTransport(webSocketClient));
        SockJsClient client = new SockJsClient(transports);
        URI uri = new URI("ws://" + "0.0.0.0" + ":" + 8080 + "/testsocket");
        SockJSTestClient testClient = new SockJSTestClient(uri, null, client, vertx, (String message) -> System.out.println(message));
        testClient.setMessageConverter(new StringMessageConverter());
        vertx.setTimer(500, x -> testComplete());
        testClient.connect(new SockJSTestMessageHandler());
        testClient.sendTextMessage("HELLO YOU");
        vertx.runOnContext(Void -> System.out.println("Hell0"));
        vertx.runOnContext(Void -> System.out.println("Hell1"));
    }

    @Override
    public void start() {
        // Make sure we call initialize() - this sets up the assert stuff so assert functionality works correctly
        initialize();
        // Deploy the module - the System property `vertx.modulename` will contain the name of the module so you
        // don't have to hardecode it in your tests
        container.deployVerticle(SockJSVerticle.class.getName(), asyncResult -> {
            // Deployment is asynchronous and this this handler will be called when it's complete (or failed)
            assertTrue(asyncResult.succeeded());
            assertNotNull("deploymentID should not be null", asyncResult.result());
            // If deployed correctly then start the tests!
            startTests();
        });
    }
}
