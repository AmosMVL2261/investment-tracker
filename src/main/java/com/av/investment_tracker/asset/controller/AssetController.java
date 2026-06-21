package com.av.investment_tracker.asset.controller;

import com.av.investment_tracker.asset.dto.AssetResponse;
import com.av.investment_tracker.asset.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Assets", description = "Asset information management")
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    @Operation(
        summary = "List all assets",
        description = "Returns all assets currently registered in the system"
    )
    public ResponseEntity<List<AssetResponse>> getAllAssets() {
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    @GetMapping("/{symbol}")
    @Operation(
        summary = "Get asset by symbol",
        description = "Returns a specific asset by its market symbol"
    )
    public ResponseEntity<AssetResponse> getAssetBySymbol(@PathVariable String symbol) {
        return ResponseEntity.ok(assetService.getAssetBySymbol(symbol));
    }

}
