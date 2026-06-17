package com.av.investment_tracker.portfolio.service;

import com.av.investment_tracker.asset.model.Asset;
import com.av.investment_tracker.asset.service.AssetService;
import com.av.investment_tracker.portfolio.dto.PortfolioEntryRequest;
import com.av.investment_tracker.portfolio.dto.PortfolioEntryResponse;
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
import java.util.List;

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
            throw new RuntimeException("Asset already exists i portfolio: "+request.getSymbol());
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
                    () -> new RuntimeException("Portfolio entry not found")
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
                () -> new RuntimeException("Portfolio entry not found")
        );

        if(!entry.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        portfolioEntryRepository.delete(entry);

    }

    public PortfolioEntryResponse getEntry(Long userId, Long entryId) {
        PortfolioEntry entry = portfolioEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Portfolio entry not found"));

        if (!entry.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        return mapToEntryResponse(entry);
    }

}
