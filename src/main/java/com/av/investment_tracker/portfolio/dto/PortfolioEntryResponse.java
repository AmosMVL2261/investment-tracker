package com.av.investment_tracker.portfolio.dto;

import com.av.investment_tracker.asset.model.AssetType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioEntryResponse {

    private Long id;
    private String symbol;
    private String name;
    private AssetType assetType;
    private BigDecimal quantity;
    private BigDecimal averageBuyPrice;
    private BigDecimal currentPrice;
    private BigDecimal totalInvested;
    private BigDecimal currentValue;
    private BigDecimal profitLoss;
    private BigDecimal profitLossPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
