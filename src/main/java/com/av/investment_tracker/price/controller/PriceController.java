package com.av.investment_tracker.price.controller;

import com.av.investment_tracker.price.dto.PriceResponse;
import com.av.investment_tracker.price.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    @GetMapping("/{symbol}")
    public ResponseEntity<PriceResponse> getPrice(@PathVariable String symbol) {
        return ResponseEntity.ok(priceService.getCurrentPrice(symbol));
    }

}
