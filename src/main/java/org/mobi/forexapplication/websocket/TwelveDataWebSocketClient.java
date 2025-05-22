package org.mobi.forexapplication.websocket;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TwelveDataWebSocketClient {

    private final StockBroadcastHandler broadcastHandler;

    public TwelveDataWebSocketClient(StockBroadcastHandler broadcastHandler) {
        this.broadcastHandler = broadcastHandler;
    }

    @Value("${twelvedata.apikey}")
    private String apiKey;
    

    @PostConstruct
    public void connect() throws URISyntaxException {
        URI uri = new URI("wss://ws.twelvedata.com/v1/quotes/price?apikey="+apiKey);

        org.java_websocket.client.WebSocketClient client = new org.java_websocket.client.WebSocketClient(uri) {
            @Override
            public void onOpen(org.java_websocket.handshake.ServerHandshake handshake) {
                System.out.println("✅ Connected to TwelveData");

                String subscribeMessage = "{ \"action\": \"subscribe\", \"params\": { \"symbols\": \"BTC/USD\" } }";

                send(subscribeMessage);

                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                scheduler.scheduleAtFixedRate(() -> {
                    if (this.isOpen()) {
                        send("{\"action\": \"heartbeat\"}");
                    }
                }, 0, 10, TimeUnit.SECONDS);

            }

            @Override
            public void onMessage(String message) {
                System.out.println("📨 Price Update: " + message);
                broadcastHandler.broadcast(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("❌ Connection Closed: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };

        client.connect();
    }
}