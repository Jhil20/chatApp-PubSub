package org.example.demo1_2.com.service;

import org.example.demo1_2.com.controller.RealTimeMessageHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig  implements WebSocketConfigurer {
    private final RealTimeMessageHandler realTimeMessageHandler;

    public WebSocketConfig(RealTimeMessageHandler realTimeMessageHandler) {
        this.realTimeMessageHandler = realTimeMessageHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler((WebSocketHandler) realTimeMessageHandler, "/ws/messages").setAllowedOrigins("*");
    }
}
