package org.mobi.forexapplication.config;

import org.mobi.forexapplication.websocket.StockBroadcastHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{
    private final StockBroadcastHandler handler;

    public WebSocketConfig(StockBroadcastHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/stock").setAllowedOrigins("*");
    }
}
