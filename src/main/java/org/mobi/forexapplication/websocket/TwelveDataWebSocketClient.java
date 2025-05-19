package org.mobi.forexapplication.websocket;

import org.springframework.stereotype.Component;

@Component
public class TwelveDataWebSocketClient {



    public void connect(){
        String url="wss://ws.twelvedata.com/v1/price?apikey=your_api_key";

    }
}
