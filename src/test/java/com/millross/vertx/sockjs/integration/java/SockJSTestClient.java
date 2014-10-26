package com.millross.vertx.sockjs.integration.java;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.vertx.java.core.Context;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VoidHandler;

import java.net.URI;
import java.nio.charset.Charset;

/**
 * User: jez
 */
public class SockJSTestClient {


    private static Log logger = LogFactory.getLog(SockJSTestClient.class);


    private final URI uri;
    private final WebSocketHttpHeaders headers;
    private final WebSocketClient webSocketClient;

    private MessageConverter messageConverter;
    private SockJSTestWebSocketHandler webSocketHandler;
    private final Context vertxContext;
    private final Handler<String> messageHandler;


    public SockJSTestClient(URI uri,
                            WebSocketHttpHeaders headers,
                            WebSocketClient webSocketClient,
                            Vertx vertx,
                            Handler<String> messageHandler) {
        this.uri = uri;
        this.headers = headers;
        this.webSocketClient = webSocketClient;
        this.messageHandler = messageHandler;
        this.vertxContext = vertx.currentContext();

    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

//    @Override
    public void connect(MessageHandler messageHandler) {
        try {
            this.webSocketHandler = new SockJSTestWebSocketHandler(messageHandler, this.messageConverter, this.vertxContext, this.messageHandler);
            this.webSocketClient.doHandshake(webSocketHandler, this.headers, this.uri).get();
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void sendTextMessage(String message) throws Exception {
        this.webSocketHandler.sendTextMessage(message);
    }


    private static class SockJSTestWebSocketHandler extends AbstractWebSocketHandler {

            private static final Charset UTF_8 = Charset.forName("UTF-8");

            private WebSocketSession session;
            private final Handler<String> textMessageHandler;
            private final Context vertxContext;

            private SockJSTestWebSocketHandler(MessageHandler delegate, MessageConverter messageConverter, Context vertxContext, Handler<String> handler) {
                this.textMessageHandler = handler;
                this.vertxContext = vertxContext;
            }

            public void sendTextMessage(String message) throws Exception {
                this.session.sendMessage(new TextMessage(message));
            }

            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                this.session = session;
                this.session.sendMessage(new TextMessage("Testing"));
                vertxContext.runOnContext(Void -> System.out.println("Hell3"));
            }

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
                vertxContext.runOnContext(getVoidHandlerFromHandler(textMessageHandler, textMessage.getPayload()));
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                logger.error("WebSocket transport error", exception);
            }

            private VoidHandler getVoidHandlerFromHandler(Handler<String> handler, String data) {
                return new VoidHandler() {
                    @Override
                    protected void handle() {
                        handler.handle(data);
                    }
                };
            }
        }

}
