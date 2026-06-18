package com.av.investment_tracker.exception;

public class AssetNotFoundException extends RuntimeException {

    public AssetNotFoundException(String symbol) {
        super("Asset not found: " + symbol);
    }

}
