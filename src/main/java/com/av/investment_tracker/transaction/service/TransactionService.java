package com.av.investment_tracker.transaction.service;

import com.av.investment_tracker.exception.PortfolioEntryNotFoundException;
import com.av.investment_tracker.portfolio.model.PortfolioEntry;
import com.av.investment_tracker.portfolio.repository.PortfolioEntryRepository;
import com.av.investment_tracker.transaction.dto.TransactionRequest;
import com.av.investment_tracker.transaction.dto.TransactionResponse;
import com.av.investment_tracker.transaction.model.Transaction;
import com.av.investment_tracker.transaction.model.TransactionType;
import com.av.investment_tracker.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PortfolioEntryRepository portfolioEntryRepository;

    public TransactionResponse addTransaction(Long userId, Long entryId, TransactionRequest request) {
        // Find the portfolio entry or throw exception if not found
        PortfolioEntry entry = portfolioEntryRepository.findById(entryId)
                                                        .orElseThrow(() -> new PortfolioEntryNotFoundException(entryId));
        // Verify the entry belongs to the authenticated user
        if(!entry.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        // If it's a sell operation, verify the user has enough units available
        // This validation occurs before updatePortfolioEntry to avoid modifying
        // the state if the operation is not valid
        if(request.getTransactionType() == TransactionType.SELL) {
            if(entry.getQuantity().compareTo(request.getQuantity()) < 0){
                throw new RuntimeException("Insufficient quantity to sell. Available: "+entry.getQuantity());
            }
        }
        // Update the entry's quantity and average price based on the transaction type
        updatePortfolioEntry(entry, request);

        // Save the historical record of the transaction
        Transaction transaction = Transaction.builder()
                .quantity(request.getQuantity())
                .portfolioEntry(entry)
                .transactionType(request.getTransactionType())
                .priceAtTransaction(request.getPriceAtTransaction())
                .build();
        return mapToResponse(transactionRepository.save(transaction));
    }

    public List<TransactionResponse> getTransactions(Long userId, Long entryId) {
        // Find the portfolio entry or throw exception if not found
        PortfolioEntry entry = portfolioEntryRepository.findById(entryId)
                .orElseThrow(() -> new PortfolioEntryNotFoundException(entryId));
        // Verify the entry belongs to the authenticated user
        if(!entry.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        // Return transactions ordered from most recent to oldest
        return transactionRepository.findByPortfolioEntryIdOrderByTransactionDateDesc(entryId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void updatePortfolioEntry(PortfolioEntry entry, TransactionRequest request) {
        if (request.getTransactionType() == TransactionType.BUY) {
            // Calculate the total value currently invested in the position
            BigDecimal currentTotal = entry.getAverageBuyPrice().multiply(entry.getQuantity());
            // Calculate the total value of the new purchase
            BigDecimal newTotal = request.getPriceAtTransaction().multiply(request.getQuantity());
            // Add the new units to the existing ones
            BigDecimal newQuantity = entry.getQuantity().add(request.getQuantity());
            // Calculate the new weighted average price:
            // (previously invested total + new purchase total) / new total quantity
            BigDecimal newAveragePrice = currentTotal.add(newTotal).divide(newQuantity, 8, RoundingMode.HALF_UP);
            entry.setQuantity(newQuantity);
            entry.setAverageBuyPrice(newAveragePrice);
        } else {
            // To sell operations, subtract the sold units
            entry.setQuantity(entry.getQuantity().subtract(request.getQuantity()));
        }
        portfolioEntryRepository.save(entry);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        // To sell operations, subtract the sold units
        BigDecimal totalValue = transaction.getPriceAtTransaction().multiply(transaction.getQuantity())
                .setScale(2, RoundingMode.HALF_UP);
        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionType(transaction.getTransactionType())
                .quantity(transaction.getQuantity())
                .priceAtTransaction(transaction.getPriceAtTransaction())
                .totalValue(totalValue)
                .transactionDate(transaction.getTransactionDate())
                .build();
    }

}
