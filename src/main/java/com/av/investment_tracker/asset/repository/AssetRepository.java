package com.av.investment_tracker.asset.repository;

import com.av.investment_tracker.asset.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findBySymbol(String symbol);
    boolean existsBySymbol(String symbol);

}
