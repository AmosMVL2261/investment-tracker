package com.av.investment_tracker.transaction.dto;

import com.av.investment_tracker.transaction.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionRequest {

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.00000001", message = "Quantity must be greater than 0")
    private BigDecimal quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00000001", message = "Price must be greater than 0")
    private BigDecimal priceAtTransaction;

}
