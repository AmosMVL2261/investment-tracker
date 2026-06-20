package com.av.investment_tracker.transaction.controller;

import com.av.investment_tracker.security.SecurityUtils;
import com.av.investment_tracker.transaction.dto.TransactionRequest;
import com.av.investment_tracker.transaction.dto.TransactionResponse;
import com.av.investment_tracker.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portfolio/{entryId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable Long entryId){
        return ResponseEntity.ok(transactionService.getTransactions(
                SecurityUtils.getAuthenticatedUserId(), entryId
        ));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> addTransaction(
            @PathVariable Long entryId,
            @RequestBody @Valid TransactionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.addTransaction(
                SecurityUtils.getAuthenticatedUserId(), entryId, request
        ));
    }

}
