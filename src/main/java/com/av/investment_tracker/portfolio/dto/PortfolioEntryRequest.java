package com.av.investment_tracker.portfolio.dto;

import com.av.investment_tracker.asset.model.AssetType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PortfolioEntryRequest {

    @NotBlank(message = "Symbol is required")
    private String symbol;

    @NotNull(message = "Asset type is required")
    private AssetType assetType;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.00000001", message = "Quantity must be greater than 0")
    private BigDecimal quantity;

    @NotNull
    @DecimalMin(value = "0.00000001", message = "Average buy price must be greater than 0")
    private BigDecimal averageBuyPrice;

}
