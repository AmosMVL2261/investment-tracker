package com.av.investment_tracker.portfolio.service;

import com.av.investment_tracker.asset.model.Asset;
import com.av.investment_tracker.asset.service.AssetService;
import com.av.investment_tracker.exception.PortfolioEntryNotFoundException;
import com.av.investment_tracker.portfolio.dto.PortfolioEntryRequest;
import com.av.investment_tracker.portfolio.dto.PortfolioEntryResponse;
import com.av.investment_tracker.portfolio.dto.PortfolioMetricsResponse;
import com.av.investment_tracker.portfolio.dto.PortfolioSummaryResponse;
import com.av.investment_tracker.portfolio.model.PortfolioEntry;
import com.av.investment_tracker.portfolio.repository.PortfolioEntryRepository;
import com.av.investment_tracker.price.dto.PriceResponse;
import com.av.investment_tracker.price.service.PriceService;
import com.av.investment_tracker.user.model.User;
import com.av.investment_tracker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final AssetService assetService;
    private final PriceService priceService;
    private final UserRepository userRepository;
    private final PortfolioEntryRepository portfolioEntryRepository;

    public PortfolioSummaryResponse getPortfolio(Long userId) {
        List<PortfolioEntry> entries = portfolioEntryRepository.findByUserId(userId);
        List<PortfolioEntryResponse> entryResponses = entries.stream()
                .map(this::mapToEntryResponse)
                .toList();

        BigDecimal totalInvested = entryResponses.stream()
                .map(PortfolioEntryResponse::getTotalInvested)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentValue = entryResponses.stream()
                .map(PortfolioEntryResponse::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProfitLoss = currentValue.subtract(totalInvested);

        BigDecimal totalProfitLossPercentage = totalInvested.compareTo(BigDecimal.ZERO) == 0 ?  BigDecimal.ZERO :
            totalProfitLoss.divide(totalInvested, 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"));

        return PortfolioSummaryResponse.builder()
                .totalInvested(totalInvested)
                .currentValue(currentValue)
                .totalProfitLoss(totalProfitLoss)
                .totalProfitLossPercentage(totalProfitLossPercentage)
                .entries(entryResponses)
                .build();
    }

    private PortfolioEntryResponse  mapToEntryResponse(PortfolioEntry entry) {
        PriceResponse priceResponse = priceService.getCurrentPrice(entry.getAsset().getSymbol());
        BigDecimal currentPrice = priceResponse.getPrice();
        BigDecimal totalInvested = entry.getAverageBuyPrice()
                                        .multiply(entry.getQuantity())
                                        .setScale(2, RoundingMode.HALF_UP);
        BigDecimal currentValue = currentPrice.multiply(entry.getQuantity())
                                                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal profitLoss = currentValue.subtract(totalInvested);

        BigDecimal profitLossPercentage = totalInvested.compareTo(BigDecimal.ZERO) == 0 ?  BigDecimal.ZERO :
                profitLoss.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));

        return PortfolioEntryResponse.builder()
                                    .id(entry.getId())
                                    .symbol(entry.getAsset().getSymbol())
                                    .name(entry.getAsset().getName())
                                    .assetType(entry.getAsset().getAssetType())
                                    .quantity(entry.getQuantity())
                                    .averageBuyPrice(entry.getAverageBuyPrice())
                                    .currentPrice(currentPrice)
                                    .totalInvested(totalInvested)
                                    .currentValue(currentValue)
                                    .profitLoss(profitLoss)
                                    .profitLossPercentage(profitLossPercentage)
                                    .createdAt(entry.getCreatedAt())
                                    .updatedAt(entry.getUpdatedAt())
                                    .build();

    }

    public PortfolioEntryResponse addEntry(Long userId, PortfolioEntryRequest request) {
        if(portfolioEntryRepository.existsByUserIdAndAssetSymbol(userId, request.getSymbol().toUpperCase())){
            throw new RuntimeException("Asset already exists in portfolio: "+request.getSymbol());
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Asset asset = assetService.findOrCreateAsset(request.getSymbol(), request.getAssetType());

        PortfolioEntry entry = PortfolioEntry.builder()
                .user(user)
                .asset(asset)
                .quantity(request.getQuantity())
                .averageBuyPrice(request.getAverageBuyPrice())
                .build();

        return mapToEntryResponse(portfolioEntryRepository.save(entry));

    }

    public PortfolioEntryResponse updateEntry(Long userId, Long entryId, PortfolioEntryRequest request) {
            PortfolioEntry entry = portfolioEntryRepository.findById(entryId).orElseThrow(
                    () -> new PortfolioEntryNotFoundException(entryId)
            );

            if(!entry.getUser().getId().equals(userId)){
                throw new RuntimeException("Unauthorized");
            }

            entry.setQuantity(request.getQuantity());
            entry.setAverageBuyPrice(request.getAverageBuyPrice());

            return mapToEntryResponse(portfolioEntryRepository.save(entry));
    }

    public  void deleteEntry(Long userId, Long entryId) {
        PortfolioEntry entry = portfolioEntryRepository.findById(entryId).orElseThrow(
                () -> new PortfolioEntryNotFoundException(entryId)
        );

        if(!entry.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        portfolioEntryRepository.delete(entry);

    }

    public PortfolioEntryResponse getEntry(Long userId, Long entryId) {
        PortfolioEntry entry = portfolioEntryRepository.findById(entryId)
                .orElseThrow(() -> new PortfolioEntryNotFoundException(entryId));

        if (!entry.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        return mapToEntryResponse(entry);
    }

    public PortfolioMetricsResponse getMetrics(Long userId) {
        List<PortfolioEntry> entries = portfolioEntryRepository.findByUserId(userId);

        // Return empty metrics if the user has no assets in their portfolio
        if(entries.isEmpty()) {
            return PortfolioMetricsResponse.builder()
                    .totalPortfolioValue(BigDecimal.ZERO)
                    .distributionByType(new HashMap<>())
                    .build();
        }

        // Map all entries to their response DTOs with real-time prices and calculations
        List<PortfolioEntryResponse> entryResponses = entries.stream()
                .map(this::mapToEntryResponse)
                .toList();

        // Find the asset with the highest profit/loss percentage
        PortfolioEntryResponse best = entryResponses.stream()
                .max(Comparator.comparing(PortfolioEntryResponse::getProfitLossPercentage))
                .orElse(null);
        // Find the asset with the lowest profit/loss percentage
        PortfolioEntryResponse worst = entryResponses.stream()
                .min(Comparator.comparing(PortfolioEntryResponse::getProfitLossPercentage))
                .orElse(null);

        // Total portfolio value: sum the current market value of all positions
        BigDecimal totalValue = entryResponses.stream()
                .map(PortfolioEntryResponse::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Distribution by asset as percentage of total portfolio
        // Group positions by asset type and sum their current values
        // Result example: {STOCK: 5000, CRYPTO: 2000, ETF: 1000}
        Map<String, BigDecimal> distribution = entryResponses.stream()
                .collect(
                    Collectors.groupingBy(
                        e -> e.getAssetType().name(),
                        Collectors.reducing(
                            BigDecimal.ZERO,
                            PortfolioEntryResponse::getCurrentValue,
                            BigDecimal::add
                        )
                )
        );

        // Convert to percentages
        // Replace absolute values with percentages relative to total portfolio value
        // Example: STOCK 5000 / 8000 total * 100 = 62.50%
        distribution.replaceAll( (type, value) ->
                totalValue.compareTo(BigDecimal.ZERO) == 0 ?
                        BigDecimal.ZERO :
                        value.divide(totalValue, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
        );

        return PortfolioMetricsResponse.builder()
                .totalPortfolioValue(totalValue)
                .bestPerformingAsset(best)
                .worstPerformingAsset(worst)
                .distributionByType(distribution)
                .build();
    }

}
