package com.av.investment_tracker.asset.service;

import com.av.investment_tracker.asset.dto.AssetResponse;
import com.av.investment_tracker.asset.model.Asset;
import com.av.investment_tracker.asset.model.AssetType;
import com.av.investment_tracker.asset.repository.AssetRepository;
import com.av.investment_tracker.exception.AssetNotFoundException;
import com.av.investment_tracker.exception.SymbolNotFoundInMarketException;
import com.av.investment_tracker.price.dto.PriceResponse;
import com.av.investment_tracker.price.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final PriceService priceService;

    public Asset findOrCreateAsset(String symbol, AssetType assetType) {
        return assetRepository.findBySymbol(symbol.toUpperCase())
                                .orElseGet(() -> createAsset(symbol.toUpperCase(), assetType));
    }

    private Asset createAsset(String symbol, AssetType assetType) {
        PriceResponse priceResponse = priceService.getCurrentPrice(symbol);

        if(priceResponse == null || priceResponse.getSymbol() == null) {
            throw new SymbolNotFoundInMarketException(symbol);
        }

        Asset asset = Asset.builder()
                            .symbol(symbol)
                            .name(priceResponse.getSymbol())
                            .assetType(assetType)
                            .build();

        return assetRepository.save(asset);
    }

    public List<AssetResponse> getAllAssets() {
        return assetRepository.findAll()
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
    }

    public AssetResponse getAssetBySymbol(String symbol) {
        Asset asset = assetRepository.findBySymbol(symbol.toUpperCase())
                                    .orElseThrow(() -> new AssetNotFoundException(symbol));
        return mapToResponse(asset);
    }

    private AssetResponse mapToResponse(Asset asset) {
        return AssetResponse.builder()
                            .id(asset.getId())
                            .symbol(asset.getSymbol())
                            .name(asset.getName())
                            .assetType(asset.getAssetType())
                            .createdAt(asset.getCreatedAt())
                            .build();
    }

}
