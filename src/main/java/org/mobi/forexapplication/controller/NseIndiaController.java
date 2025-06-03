package org.mobi.forexapplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mobi.forexapplication.model.IndexEquityInfo;
import org.mobi.forexapplication.service.NseIndiaService;
import org.mobi.forexapplication.utils.IndexEquityInfoMapper;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/nse")
@CrossOrigin // Enable CORS for Angular
public class NseIndiaController {
    @Autowired
    private NseIndiaService nseIndiaService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    //1. get stock info
    @GetMapping("/equity-details/{symbol}")
    public Mono<String> getEquityDetails(@PathVariable String symbol) {
        return nseIndiaService.getEquityDetails(symbol);
    }
    //2. get stock intraday info
    @GetMapping("/equity-details/intraday/{symbol}")
    public Mono<String> getEquityIntradayDetails(@PathVariable String symbol) {
        return nseIndiaService.getEquityIntradayDetails(symbol);
    }
    //3. get stock announcements
    @GetMapping("/equity-details/announcements/{symbol}")
    public Mono<String> getEquityAnnouncements(@PathVariable String symbol) {
        return nseIndiaService.getEquityAnnouncements(symbol);
    }
    //4. get market status
    @GetMapping("/getMarketStatus")
    public Mono<String> getMarketStatus()
    {return nseIndiaService.getMarketStatus();}

    //7. get equity chart details( for candle )
    @GetMapping("/getCandle/{symbol}")
    public Mono<String> getChartPoints(@PathVariable String symbol){
        return nseIndiaService.getChartPoints(symbol);
    }

    
    //7. get equity chart details( for candle ) with pre-open data
    @GetMapping("/getCandleWithPO/{symbol}")
    public Mono<String> getChartPointsWithPreopen(@PathVariable String symbol){
        return nseIndiaService.getChartPointsWithPreopen(symbol);
    }

    @GetMapping("/equity-stock-indices/{index}")
    public Mono<String> getEquityStockIndices(@PathVariable String index) {
        return nseIndiaService.getEquityStockIndices(index);
    }
}