package com.av.investment_tracker.transaction.model;

import com.av.investment_tracker.portfolio.model.PortfolioEntry;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_entry_id", nullable = false)
    private PortfolioEntry portfolioEntry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal priceAtTransaction;

    @Column(nullable = false, updatable = false)
    private LocalDateTime transactionDate;

    @PrePersist
    protected void onCreate() {
        this.transactionDate = LocalDateTime.now();
    }

}
