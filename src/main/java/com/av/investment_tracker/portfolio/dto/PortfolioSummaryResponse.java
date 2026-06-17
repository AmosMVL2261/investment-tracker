package com.av.investment_tracker.portfolio.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioSummaryResponse {

    private BigDecimal totalInvested;
    private BigDecimal currentValue;
    private BigDecimal totalProfitLoss;
    private BigDecimal totalProfitLossPercentage;
    private List<PortfolioEntryResponse> entries;

}
