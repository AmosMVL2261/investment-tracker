package com.av.investment_tracker;

import com.av.investment_tracker.price.client.AlphaVantageClient;
import com.av.investment_tracker.price.dto.PriceResponse;
import com.av.investment_tracker.price.service.PriceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private AlphaVantageClient alphaVantageClient;

    @InjectMocks
    private PriceService priceService;

    @Test
    void getCurrentPrice_ShouldReturnPrice_WhenValidSymbol() {
        // Arrange
        PriceResponse mockPrice = new PriceResponse();
        mockPrice.setSymbol("AAPL");
        mockPrice.setPrice(new BigDecimal("200.00"));
        mockPrice.setChange(new BigDecimal("1.50"));
        mockPrice.setChangePercent("0.75%");

        when(alphaVantageClient.getPrice("AAPL")).thenReturn(mockPrice);

        // Act
        PriceResponse response = priceService.getCurrentPrice("AAPL");

        // Assert
        assertNotNull(response);
        assertEquals("AAPL", response.getSymbol());
        assertEquals(new BigDecimal("200.00"), response.getPrice());
        verify(alphaVantageClient).getPrice("AAPL");
    }

    @Test
    void getCurrentPrice_ShouldNormalizeSymbolToUpperCase() {
        // Arrange
        PriceResponse mockPrice = new PriceResponse();
        mockPrice.setSymbol("AAPL");
        mockPrice.setPrice(new BigDecimal("200.00"));

        when(alphaVantageClient.getPrice("AAPL")).thenReturn(mockPrice);

        // Act
        PriceResponse response = priceService.getCurrentPrice("aapl");

        // Assert
        assertNotNull(response);
        // Verify that the client was called with uppercase symbol regardless of input
        verify(alphaVantageClient).getPrice("AAPL");
    }

    @Test
    void getCurrentPrice_ShouldThrowException_WhenClientFails() {
        // Arrange
        when(alphaVantageClient.getPrice("INVALID"))
                .thenThrow(new RuntimeException("Symbol not found in market: INVALID"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> priceService.getCurrentPrice("INVALID"));
    }
}