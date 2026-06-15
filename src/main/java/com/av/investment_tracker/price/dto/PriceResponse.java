package com.av.investment_tracker.price.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PriceResponse {

    private String symbol;
    private BigDecimal price;
    private BigDecimal change;
    private String changePercent;

}
