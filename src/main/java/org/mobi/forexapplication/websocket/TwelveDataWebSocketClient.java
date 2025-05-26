package org.mobi.forexapplication.websocket;

import jakarta.annotation.PostConstruct;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.*;

@Component
public class TwelveDataWebSocketClient {

    private final StockBroadcastHandler broadcastHandler;

    @Value("${twelvedata.apikey}")
    private String apiKey;

    private WebSocketClient client;
    private ScheduledExecutorService scheduler;
    private final String symbolsJson = "[{\"symbol\":\"BTC/USD\"},{\"symbol\":\"EUR/USD\"},{\"symbol\":\"XAU/USD\"},{\"symbol\":\"1299\"}]";

    public TwelveDataWebSocketClient(StockBroadcastHandler broadcastHandler) {
        this.broadcastHandler = broadcastHandler;
    }

    @PostConstruct
    public void start() {
        connectWebSocket();
    }

    private void connectWebSocket() {
        try {
            URI uri = new URI("wss://ws.twelvedata.com/v1/quotes/price?apikey=" + apiKey);

            client = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("✅ Connected to TwelveData");

                    // Subscribe to symbols
                    String subscribeMessage = "{ \"action\": \"subscribe\", \"params\": { \"symbols\": " + symbolsJson + " } }";
                    send(subscribeMessage);

                    // Start heartbeat scheduler
                    if (scheduler == null || scheduler.isShutdown()) {
                        scheduler = Executors.newSingleThreadScheduledExecutor();
                        scheduler.scheduleAtFixedRate(() -> {
                            if (isOpen()) {
                                send("{\"action\": \"heartbeat\"}");
                            }
                        }, 10, 10, TimeUnit.SECONDS); // Delay heartbeat start
                    }
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("📨 Price Update: " + message);
                    broadcastHandler.broadcast(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("❌ Connection Closed: " + reason);
                    cleanupAndReconnect();
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println("⚠️ WebSocket Error: " + ex.getMessage());
                    cleanupAndReconnect();
                }
            };

            client.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void cleanupAndReconnect() {
        try {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdownNow();
            }
            if (client != null && !client.isOpen()) {
                // Wait before retrying
                TimeUnit.SECONDS.sleep(5);
                connectWebSocket();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
