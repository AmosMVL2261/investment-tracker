package com.av.investment_tracker.exception;

public class SymbolNotFoundInMarketException extends RuntimeException {

    public SymbolNotFoundInMarketException(String symbol) {
        super("Symbol not found in market: " + symbol);
    }

}
