package com.av.investment_tracker;

import com.av.investment_tracker.asset.model.Asset;
import com.av.investment_tracker.asset.model.AssetType;
import com.av.investment_tracker.portfolio.model.PortfolioEntry;
import com.av.investment_tracker.portfolio.repository.PortfolioEntryRepository;
import com.av.investment_tracker.transaction.dto.TransactionRequest;
import com.av.investment_tracker.transaction.dto.TransactionResponse;
import com.av.investment_tracker.transaction.model.Transaction;
import com.av.investment_tracker.transaction.model.TransactionType;
import com.av.investment_tracker.transaction.repository.TransactionRepository;
import com.av.investment_tracker.transaction.service.TransactionService;
import com.av.investment_tracker.user.model.Role;
import com.av.investment_tracker.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PortfolioEntryRepository portfolioEntryRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User mockUser;
    private Asset mockAsset;
    private PortfolioEntry mockEntry;

    @BeforeEach
    void setUp(){
        mockUser = User.builder()
                .id(1L)
                .username("someone")
                .email("someone@email.com")
                .password("hashedPassword")
                .role(Role.ROLE_USER)
                .build();

        mockAsset = Asset.builder()
                .id(1L)
                .symbol("AAPL")
                .name("AAPL")
                .assetType(AssetType.STOCK)
                .build();

        mockEntry = PortfolioEntry.builder()
                .id(1L)
                .user(mockUser)
                .asset(mockAsset)
                .quantity(new BigDecimal("10"))
                .averageBuyPrice(new BigDecimal("150.00"))
                .build();
    }

    @Test
    void addTransaction_ShouldUpdateAverageBuyPrice_WhenBuyTransaction() {
        //Arrange
        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(TransactionType.BUY);
        request.setQuantity(new BigDecimal("5"));
        request.setPriceAtTransaction(new BigDecimal("200.00"));

        Transaction savedTransaction = Transaction.builder()
                .id(1L)
                .portfolioEntry(mockEntry)
                .transactionType(TransactionType.BUY)
                .quantity(new BigDecimal("5"))
                .priceAtTransaction(new BigDecimal("200.00"))
                .build();

        when(portfolioEntryRepository.findById(1L)).thenReturn(Optional.of(mockEntry));
        when(portfolioEntryRepository.save(any(PortfolioEntry.class))).thenReturn(mockEntry);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // Act
        TransactionResponse response = transactionService.addTransaction(1L, 1L, request);

        // Assert
        assertNotNull(response);
        // Verify average price updated: (10*150 + 5*200) / 15 = 166.66666667
        assertEquals(new BigDecimal("166.66666667"), mockEntry.getAverageBuyPrice());
        // Verify quantity updated: 10 + 5 = 15
        assertEquals(new BigDecimal("15"), mockEntry.getQuantity());
        assertEquals(TransactionType.BUY, response.getTransactionType());

    }

    @Test
    void addTransaction_ShouldReduceQuantity_WhenSellTransaction() {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(TransactionType.SELL);
        request.setQuantity(new BigDecimal("3"));
        request.setPriceAtTransaction(new BigDecimal("200.00"));

        Transaction savedTransaction = Transaction.builder()
                .id(1L)
                .portfolioEntry(mockEntry)
                .transactionType(TransactionType.SELL)
                .quantity(new BigDecimal("3"))
                .priceAtTransaction(new BigDecimal("200.00"))
                .build();

        when(portfolioEntryRepository.findById(1L)).thenReturn(Optional.of(mockEntry));
        when(portfolioEntryRepository.save(any(PortfolioEntry.class))).thenReturn(mockEntry);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // Act
        TransactionResponse response = transactionService.addTransaction(1L, 1L, request);

        // Assert
        assertNotNull(response);
        // Verify quantity reduced: 10 - 3 = 7
        assertEquals(new BigDecimal("7"), mockEntry.getQuantity());
        // Verify average price unchanged after sell
        assertEquals(new BigDecimal("150.00"), mockEntry.getAverageBuyPrice());
        assertEquals(TransactionType.SELL, response.getTransactionType());
    }

    @Test
    void addTransaction_ShouldThrowException_WhenInsufficientQuantity() {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(TransactionType.SELL);
        request.setQuantity(new BigDecimal("15"));
        request.setPriceAtTransaction(new BigDecimal("200.00"));

        when(portfolioEntryRepository.findById(1L)).thenReturn(Optional.of(mockEntry));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> transactionService.addTransaction(1L, 1L, request));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void addTransaction_ShouldThrowException_WhenUnauthorizedUser() {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(TransactionType.BUY);
        request.setQuantity(new BigDecimal("5"));
        request.setPriceAtTransaction(new BigDecimal("200.00"));

        when(portfolioEntryRepository.findById(1L)).thenReturn(Optional.of(mockEntry));

        // Act & Assert
        // userId 2 tries to access entry belonging to userId 1
        assertThrows(RuntimeException.class,
                () -> transactionService.addTransaction(2L, 1L, request));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
