package com.millross.vertx.sockjs.integration.java;

import com.millross.vertx.sockjs.SockJSVerticle;
import org.junit.Test;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;
import org.springframework.web.socket.sockjs.frame.SockJsMessageCodec;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;
import org.vertx.testtools.TestVerticle;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.vertx.testtools.VertxAssert.*;

/**
 * User: jez
 */
public class SockJSIntegrationTest extends TestVerticle{

    private class TestSockJSServiceConfig implements SockJsServiceConfig {

        private final TaskScheduler scheduler = new ThreadPoolTaskScheduler();
        private SockJsMessageCodec codec = new Jackson2SockJsMessageCodec();

        @Override
        public TaskScheduler getTaskScheduler() {
            return scheduler;
        }

        @Override
        public int getStreamBytesLimit() {
            return (128 * 1024);
        }

        @Override
        public long getHeartbeatTime() {
            return 25000;
        }

        @Override
        public int getHttpMessageCacheSize() {
            return 100;
        }

        @Override
        public SockJsMessageCodec getMessageCodec() {
            return codec;
        }
    }

    @Test
    public void testSockJSWithClient() throws Exception {
        List<Transport> transports = new ArrayList<>();
        JettyWebSocketClient webSocketClient = new JettyWebSocketClient();
        webSocketClient.start();
        transports.add(new WebSocketTransport(webSocketClient));
        SockJsClient client = new SockJsClient(transports);
        URI uri = new URI("ws://" + "0.0.0.0" + ":" + 8080 + "/testsocket");
        SockJsServiceConfig config = new TestSockJSServiceConfig();
        SockJSTestClient testClient = new SockJSTestClient(uri, null, client);
        testClient.setMessageConverter(new StringMessageConverter());
        testClient.connect(new SockJSTestMessageHandler());
        testClient.sendTextMessage("HELLO YOU");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testComplete();
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
