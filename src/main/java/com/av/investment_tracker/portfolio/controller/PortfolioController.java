package com.av.investment_tracker.portfolio.controller;

import com.av.investment_tracker.portfolio.dto.PortfolioEntryRequest;
import com.av.investment_tracker.portfolio.dto.PortfolioEntryResponse;
import com.av.investment_tracker.portfolio.dto.PortfolioSummaryResponse;
import com.av.investment_tracker.portfolio.service.PortfolioService;
import com.av.investment_tracker.security.UserPrincipal;
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
public class PortfolioController {

    private final PortfolioService portfolioService;

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        if (!(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            throw new RuntimeException("Invalid authentication principal");
        }
        return userPrincipal.getUser().getId();
    }

    @GetMapping
    public ResponseEntity<PortfolioSummaryResponse> getPortfolio() {
        return ResponseEntity.ok(portfolioService.getPortfolio(getAuthenticatedUserId()));
    }

    @PostMapping
    public ResponseEntity<PortfolioEntryResponse> addEntry(@RequestBody @Valid PortfolioEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                portfolioService.addEntry(getAuthenticatedUserId(), request)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<PortfolioEntryResponse> updateEntry(
            @PathVariable Long id,
            @RequestBody @Valid PortfolioEntryRequest request
    ) {
        return ResponseEntity.ok(portfolioService.updateEntry(getAuthenticatedUserId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        portfolioService.deleteEntry(getAuthenticatedUserId(), id);
        return ResponseEntity.noContent().build();
    }

}
