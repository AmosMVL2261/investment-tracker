package com.av.investment_tracker.price.controller;

import com.av.investment_tracker.price.dto.PriceResponse;
import com.av.investment_tracker.price.service.PriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
@Tag(name = "Prices", description = "Real-time market price queries")
public class PriceController {

    private final PriceService priceService;

    @GetMapping("/{symbol}")
    @Operation(
        summary = "Get current price",
        description = "Returns the current market price for a given symbol via Alpha Vantage API. Results are cached to optimize API usage"
    )
    public ResponseEntity<PriceResponse> getPrice(@PathVariable String symbol) {
        return ResponseEntity.ok(priceService.getCurrentPrice(symbol));
    }

}
