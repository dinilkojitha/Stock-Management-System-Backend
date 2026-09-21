package com.example.stockmanagementsystembackend.domain.inventory.service;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.repository.InventoryItemRepository;
import com.example.stockmanagementsystembackend.domain.stock.repository.StockRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
public class InventoryDashboardService {
    private final InventoryItemRepository inventoryItems;
    private final StockRepository stocks;
    private final Clock clock;

    public InventoryDashboardService(
            InventoryItemRepository inventoryItems,
            StockRepository stocks,
            @Qualifier("stockBatchClock") Clock clock
    ) {
        this.inventoryItems = inventoryItems;
        this.stocks = stocks;
        this.clock = clock;
    }

    /**
     * Calculates a current read-only snapshot; none of these values are persisted.
     */
    @Transactional(readOnly = true)
    public InventoryDashboardSummary getSummary() {
        List<InventoryItem> items = inventoryItems.findAll();
        LocalDate today = LocalDate.now(clock);

        double totalQuantity = items.stream()
                .map(InventoryItem::getTotalQuantity)
                .filter(value -> value != null)
                .mapToDouble(Double::doubleValue)
                .sum();

        BigDecimal totalInventoryValue = items.stream()
                .map(this::valuationOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new InventoryDashboardSummary(
                items.size(),
                totalQuantity,
                inventoryItems.findLowStock().size(),
                stocks.findByExpiryDateBeforeOrderByExpiryDateAscStockIdAsc(today).size(),
                stocks.findByExpiryDateBetweenOrderByExpiryDateAscStockIdAsc(today, today.plusDays(30)).size(),
                totalInventoryValue
        );
    }

    private BigDecimal valuationOf(InventoryItem item) {
        if (item.getTotalQuantity() == null || item.getUnitPrice() == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(item.getTotalQuantity())
                .multiply(BigDecimal.valueOf(item.getUnitPrice()));
    }

    // A typed response keeps the REST contract explicit without adding database fields.
    public static class InventoryDashboardSummary {
        private final long totalItems;
        private final double totalQuantity;
        private final long lowStockItems;
        private final long expiredBatches;
        private final long expiringSoonBatches;
        private final BigDecimal totalInventoryValue;

        public InventoryDashboardSummary(long totalItems, double totalQuantity, long lowStockItems,
                                         long expiredBatches, long expiringSoonBatches,
                                         BigDecimal totalInventoryValue) {
            this.totalItems = totalItems;
            this.totalQuantity = totalQuantity;
            this.lowStockItems = lowStockItems;
            this.expiredBatches = expiredBatches;
            this.expiringSoonBatches = expiringSoonBatches;
            this.totalInventoryValue = totalInventoryValue;
        }

        public long getTotalItems() {
            return totalItems;
        }

        public double getTotalQuantity() {
            return totalQuantity;
        }

        public long getLowStockItems() {
            return lowStockItems;
        }

        public long getExpiredBatches() {
            return expiredBatches;
        }

        public long getExpiringSoonBatches() {
            return expiringSoonBatches;
        }

        public BigDecimal getTotalInventoryValue() {
            return totalInventoryValue;
        }
    }
}
