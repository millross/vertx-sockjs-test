package com.millross.vertx.sockjs.integration.java;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * User: jez
 */
public class SockJSTestMessageHandler implements MessageHandler {
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        System.out.println("Message received:-");
        System.out.println(message.getPayload().toString());
    }
}
