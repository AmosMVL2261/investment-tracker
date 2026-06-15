package com.av.investment_tracker.asset.dto;

import com.av.investment_tracker.asset.model.AssetType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetResponse {

    private Long id;
    private String symbol;
    private String name;
    private AssetType assetType;
    private LocalDateTime createdAt;

}
