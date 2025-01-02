package org.example.demo1_2.com.controller;

import org.example.demo1_2.com.service.PubSubSubscriberService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class RealTimeMessageHandler extends TextWebSocketHandler {
    private final PubSubSubscriberService pubSubSubscriberService;

    public RealTimeMessageHandler(PubSubSubscriberService pubSubSubscriberService) {
        this.pubSubSubscriberService = pubSubSubscriberService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        new Thread(() -> {
            while (session.isOpen()) {
                pubSubSubscriberService.pullMessages(session);
                try {
                    Thread.sleep(1000); // Polling interval
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Handle client messages if needed
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Handle session close if needed
    }
}
