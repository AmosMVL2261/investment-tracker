package com.av.investment_tracker.exception;

public class PortfolioEntryNotFoundException extends RuntimeException {

    public PortfolioEntryNotFoundException(Long id) {
        super("Portfolio entry not found with id: " + id);
    }

}
