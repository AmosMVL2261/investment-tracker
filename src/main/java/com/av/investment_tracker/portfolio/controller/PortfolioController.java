package com.av.investment_tracker.portfolio.controller;

import com.av.investment_tracker.portfolio.dto.PortfolioEntryRequest;
import com.av.investment_tracker.portfolio.dto.PortfolioEntryResponse;
import com.av.investment_tracker.portfolio.dto.PortfolioMetricsResponse;
import com.av.investment_tracker.portfolio.dto.PortfolioSummaryResponse;
import com.av.investment_tracker.portfolio.service.PortfolioService;
import com.av.investment_tracker.security.SecurityUtils;
import com.av.investment_tracker.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portfolio")
@RequiredArgsConstructor
@Tag(name = "Portfolio", description = "Portfolio management")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping
    @Operation(
        summary = "Get portfolio summary",
        description = "Returns the full portfolio with real-time prices, profit/loss calculations and overall summary"
    )
    public ResponseEntity<PortfolioSummaryResponse> getPortfolio() {
        return ResponseEntity.ok(portfolioService.getPortfolio(SecurityUtils.getAuthenticatedUserId()));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get portfolio entry",
        description = "Returns a specific portfolio entry with real-time price and profit/loss calculation"
    )
    public ResponseEntity<PortfolioEntryResponse> getEntry(@PathVariable Long id) {
        return ResponseEntity.ok(portfolioService.getEntry(SecurityUtils.getAuthenticatedUserId(), id));
    }

    @PostMapping
    @Operation(
        summary = "Add asset to portfolio",
        description = "Adds a new asset to the portfolio. The asset is validated against Alpha Vantage API"
    )
    public ResponseEntity<PortfolioEntryResponse> addEntry(@RequestBody @Valid PortfolioEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                portfolioService.addEntry(SecurityUtils.getAuthenticatedUserId(), request)
        );
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update portfolio entry",
        description = "Updates the quantity and average buy price of an existing portfolio entry"
    )
    public ResponseEntity<PortfolioEntryResponse> updateEntry(
            @PathVariable Long id,
            @RequestBody @Valid PortfolioEntryRequest request
    ) {
        return ResponseEntity.ok(portfolioService.updateEntry(SecurityUtils.getAuthenticatedUserId(), id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete portfolio entry",
        description = "Removes an asset from the portfolio along with all its transaction history"
    )
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        portfolioService.deleteEntry(SecurityUtils.getAuthenticatedUserId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/metrics")
    @Operation(
        summary = "Get portfolio metrics",
        description = "Returns best and worst performing assets and distribution by asset type"
    )
    public ResponseEntity<PortfolioMetricsResponse> getMetrics() {
        return ResponseEntity.ok(portfolioService.getMetrics(SecurityUtils.getAuthenticatedUserId()));
    }

}
