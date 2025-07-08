package org.mobi.forexapplication.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NseIndiaService {

    private final ObjectMapper mapper = new ObjectMapper();

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private final WebClient webClient;
    private final AtomicReference<String> cookies = new AtomicReference<>("");
    private final AtomicReference<String> userAgent = new AtomicReference<>(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
    );

    public NseIndiaService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://www.nseindia.com")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.REFERER, "https://www.nseindia.com/")
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent.get())
                .build();
    }

    private Mono<Object> refreshCookies() {
        return webClient.get()
                .uri("/get-quotes/equity?symbol=TCS")
                .exchangeToMono(response -> {
                    List<String> setCookies = response.headers().header(HttpHeaders.SET_COOKIE);
                    if (!setCookies.isEmpty()) {
                        String joinedCookies = String.join("; ",
                                setCookies.stream().map(cookie -> cookie.split(";")[0]).toList());
                        cookies.set(joinedCookies);
                    }
                    return Mono.empty();
                })
                .timeout(REQUEST_TIMEOUT);
    }

    private Mono<String> performNseRequest(String urlPath) {
        return webClient.get()
                .uri(urlPath)
                .header(HttpHeaders.COOKIE, cookies.get())
                .header(HttpHeaders.USER_AGENT, userAgent.get())
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class).flatMap(body -> {
                            System.err.println("Error from NSE: " + body);
                            return Mono.error(new RuntimeException("NSE API Error: " + body));
                        }))
                .bodyToMono(String.class)
                .timeout(REQUEST_TIMEOUT);
    }

    private Mono<String> safeRequest(String urlPath) {
        if (cookies.get().isEmpty()) {
            return refreshCookies().then(performNseRequest(urlPath));
        } else {
            return performNseRequest(urlPath)
                    .onErrorResume(err -> refreshCookies().then(performNseRequest(urlPath)));
        }
    }

    // API Methods
    public Mono<String> getEquityDetails(String symbol) {
        return safeRequest(String.format("/api/quote-equity?symbol=%s", symbol.toUpperCase()));
    }

    public BigDecimal fetchLastPrice(String symbol) {
        String json = getEquityDetails(symbol)     // Mono<String>
                .block(REQUEST_TIMEOUT);           // turn it into String

        if (json == null) {
            throw new RuntimeException("No response from NSE for " + symbol);
        }
        try {
            JsonNode root       = mapper.readTree(json);
            JsonNode lastPriceN = root.path("priceInfo").path("lastPrice");

            if (!lastPriceN.isNumber()) {
                throw new IllegalStateException(
                        "priceInfo.lastPrice missing for " + symbol);
            }
            return lastPriceN.decimalValue();      // BigDecimal
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to parse lastPrice for " + symbol, ex);
        }
    }

    public Mono<String> getEquityIntradayDetails(String symbol) {
        return safeRequest(String.format("/api/quote-equity?symbol=%s§ion=trade_info", symbol.toUpperCase()));
    }

    public Mono<String> getEquityAnnouncements(String symbol) {
        return safeRequest(String.format("/api/top-corp-info?symbol=%s&market=equities", symbol.toUpperCase()));
    }

    public Mono<String> getMarketStatus() {
        return safeRequest("/api/marketStatus");
    }


    public Mono<String> getChartPoints(String symbol) {
        return safeRequest(String.format("/api/chart-databyindex?index=%s&indices=true&preopen=false", symbol.toUpperCase()));
    }

    public Mono<String> getChartPointsWithPreopen(String symbol) {
        return safeRequest(String.format("/api/chart-databyindex?index=%s&indices=true&preopen=true", symbol.toUpperCase()));
    }

    public Mono<String> getEquityStockIndices(String index) {
        return safeRequest(String.format("/api/equity-stockIndices?index=%s", index.toUpperCase()));
    }
}
