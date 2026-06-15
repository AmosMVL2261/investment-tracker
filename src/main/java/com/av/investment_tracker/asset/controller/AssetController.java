package com.av.investment_tracker.asset.controller;

import com.av.investment_tracker.asset.dto.AssetResponse;
import com.av.investment_tracker.asset.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    public ResponseEntity<List<AssetResponse>> getAllAssets() {
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<AssetResponse> getAssetBySymbol(@PathVariable String symbol) {
        return ResponseEntity.ok(assetService.getAssetBySymbol(symbol));
    }

}
