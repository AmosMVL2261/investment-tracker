package com.av.investment_tracker.price.client;

import com.av.investment_tracker.price.dto.PriceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AlphaVantageClient {

    @Value("${alphavantage.api-key}")
    private String apiKey;

    @Value("${alphavantage.base-url}")
    private String baseUrl;

    private final RestClient restClient;

    public PriceResponse getPrice(String symbol) {
        Map<String, Object> response = restClient.get()
                .uri(baseUrl + "?function=GLOBAL_QUOTE&symbol={symbol}&apikey={apiKey}", symbol, apiKey)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        if(response == null || !response.containsKey("Global Quote")) {
            throw new RuntimeException("Invalid response from Alpha Vantage");
        }

        @SuppressWarnings("unchecked")
        Map<String, String> quote = (Map<String, String>) response.get("Global Quote");

        if(quote.isEmpty()) {
            throw new RuntimeException("Symbol not found: "+symbol);
        }
        PriceResponse priceResponse = new PriceResponse();
        priceResponse.setSymbol(quote.get("01. symbol"));
        priceResponse.setPrice(new BigDecimal(quote.get("05. price").trim()));
        priceResponse.setChange(new BigDecimal(quote.get("09. change").trim()));
        priceResponse.setChangePercent(quote.get("10. change percent").trim());


        return priceResponse;
    }

}
