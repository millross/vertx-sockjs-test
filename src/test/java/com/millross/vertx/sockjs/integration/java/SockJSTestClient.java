package com.millross.vertx.sockjs.integration.java;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

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
    private MessageHandler messageHandler;


    public SockJSTestClient(URI uri, WebSocketHttpHeaders headers, WebSocketClient webSocketClient) {
        this.uri = uri;
        this.headers = headers;
        this.webSocketClient = webSocketClient;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

//    @Override
    public void connect(MessageHandler messageHandler) {
        try {
            this.webSocketHandler = new SockJSTestWebSocketHandler(messageHandler, this.messageConverter);
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

            private final MessageConverter messageConverter;
            private final MessageHandler messageHandler;

            private WebSocketSession session;

            private SockJSTestWebSocketHandler(MessageHandler delegate, MessageConverter messageConverter) {
                this.messageHandler = delegate;
                this.messageConverter = messageConverter;
            }

            public void sendTextMessage(String message) throws Exception {
                this.session.sendMessage(new TextMessage(message));
            }

            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                this.session = session;
                this.session.sendMessage(new TextMessage("Testing"));
            }

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
                System.out.println(textMessage.getPayload());
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                logger.error("WebSocket transport error", exception);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//                this.stompMessageHandler.afterDisconnected();
            }
        }

}
