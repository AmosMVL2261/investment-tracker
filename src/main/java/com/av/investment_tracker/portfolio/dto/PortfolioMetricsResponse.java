package com.av.investment_tracker.portfolio.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioMetricsResponse {

    private BigDecimal totalPortfolioValue;
    private PortfolioEntryResponse bestPerformingAsset;
    private PortfolioEntryResponse worstPerformingAsset;
    private Map<String, BigDecimal> distributionByType;

}
