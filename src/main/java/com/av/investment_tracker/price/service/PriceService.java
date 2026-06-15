package com.av.investment_tracker.price.service;

import com.av.investment_tracker.price.client.AlphaVantageClient;
import com.av.investment_tracker.price.dto.PriceResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final AlphaVantageClient alphaVantageClient;

    @Cacheable(value = "prices", key = "#symbol")
    public PriceResponse getCurrentPrice(String symbol) {
        return alphaVantageClient.getPrice(symbol.toUpperCase());
    }

}
