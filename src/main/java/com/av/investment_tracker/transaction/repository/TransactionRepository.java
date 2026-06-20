package com.av.investment_tracker.transaction.repository;

import com.av.investment_tracker.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByPortfolioEntryId(Long portfolioEntryId);
    List<Transaction> findByPortfolioEntryIdOrderByTransactionDateDesc(Long portfolioEntryId);

}
