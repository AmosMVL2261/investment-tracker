package com.av.investment_tracker;

import com.av.investment_tracker.asset.model.Asset;
import com.av.investment_tracker.asset.model.AssetType;
import com.av.investment_tracker.asset.service.AssetService;
import com.av.investment_tracker.portfolio.dto.PortfolioEntryRequest;
import com.av.investment_tracker.portfolio.dto.PortfolioSummaryResponse;
import com.av.investment_tracker.portfolio.model.PortfolioEntry;
import com.av.investment_tracker.portfolio.repository.PortfolioEntryRepository;
import com.av.investment_tracker.portfolio.service.PortfolioService;
import com.av.investment_tracker.price.dto.PriceResponse;
import com.av.investment_tracker.price.service.PriceService;
import com.av.investment_tracker.user.model.Role;
import com.av.investment_tracker.user.model.User;
import com.av.investment_tracker.user.repository.UserRepository;
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
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioEntryRepository portfolioEntryRepository;

    @Mock
    private AssetService assetService;

    @Mock
    private PriceService priceService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    private User mockUser;
    private Asset mockAsset;
    private PortfolioEntry mockEntry;

    @BeforeEach
    void setUp() {
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
    void getPortfolio_ShouldCalculateProfitLossCorrectly() {
        // Arrange
        PriceResponse mockPrice = new PriceResponse();
        mockPrice.setSymbol("AAPL");
        mockPrice.setPrice(new BigDecimal("200.00"));

        when(portfolioEntryRepository.findByUserId(1L)).thenReturn(List.of(mockEntry));
        when(priceService.getCurrentPrice("AAPL")).thenReturn(mockPrice);

        // Act
        PortfolioSummaryResponse response = portfolioService.getPortfolio(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getEntries().size());
        assertEquals(new BigDecimal("1500.00"), response.getTotalInvested());
        assertEquals(new BigDecimal("2000.00"), response.getCurrentValue());
        assertEquals(new BigDecimal("500.00"), response.getTotalProfitLoss());
    }

    @Test
    void getPortfolio_ShouldReturnNegativeProfitLoss_WhenPriceFellBelowAverage() {
        // Arrange
        PriceResponse mockPrice = new PriceResponse();
        mockPrice.setSymbol("AAPL");
        mockPrice.setPrice(new BigDecimal("100.00"));

        when(portfolioEntryRepository.findByUserId(1L)).thenReturn(List.of(mockEntry));
        when(priceService.getCurrentPrice("AAPL")).thenReturn(mockPrice);

        // Act
        PortfolioSummaryResponse response = portfolioService.getPortfolio(1L);

        // Assert
        assertTrue(response.getTotalProfitLoss().compareTo(BigDecimal.ZERO) < 0);
        assertEquals(new BigDecimal("-500.00"), response.getTotalProfitLoss());
    }

    @Test
    void addEntry_ShouldThrowException_WhenAssetAlreadyExistsInPortfolio() {
        // Arrange
        PortfolioEntryRequest request = new PortfolioEntryRequest();
        request.setSymbol("AAPL");
        request.setAssetType(AssetType.STOCK);
        request.setQuantity(new BigDecimal("5"));
        request.setAverageBuyPrice(new BigDecimal("150.00"));

        when(portfolioEntryRepository.existsByUserIdAndAssetSymbol(1L, "AAPL"))
                .thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> portfolioService.addEntry(1L, request));
        verify(portfolioEntryRepository, never()).save(any(PortfolioEntry.class));
    }
}