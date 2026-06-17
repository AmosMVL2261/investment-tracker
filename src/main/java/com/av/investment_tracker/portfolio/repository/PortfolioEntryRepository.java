package com.av.investment_tracker.portfolio.repository;

import com.av.investment_tracker.portfolio.model.PortfolioEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioEntryRepository extends JpaRepository<PortfolioEntry, Long> {

    List<PortfolioEntry> findByUserId(Long userId);
    Optional<PortfolioEntry> findByUserIdAndAssetSymbol(Long userId, String symbol);
    boolean existsByUserIdAndAssetSymbol(Long userId, String symbol);

}
