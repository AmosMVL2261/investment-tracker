package com.av.investment_tracker.transaction.dto;

import com.av.investment_tracker.transaction.model.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;
    private TransactionType transactionType;
    private BigDecimal quantity;
    private BigDecimal priceAtTransaction;
    private BigDecimal totalValue;
    private LocalDateTime transactionDate;

}
