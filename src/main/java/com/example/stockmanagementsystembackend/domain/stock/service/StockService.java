package com.example.stockmanagementsystembackend.domain.stock.service;

import com.example.stockmanagementsystembackend.domain.stock.repository.StockRepository;
import com.example.stockmanagementsystembackend.domain.stock.entity.Stock;
import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.repository.InventoryItemRepository;
import com.example.stockmanagementsystembackend.domain.inventory.service.CrudService;
import com.example.stockmanagementsystembackend.domain.organization.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class StockService implements CrudService<Stock, Integer> {
    private final StockRepository stocks;
    private final BranchRepository branches;
    private final InventoryItemRepository items;
    private final Clock clock;

    public StockService(StockRepository stocks, BranchRepository branches,
                        InventoryItemRepository items, @Qualifier("stockBatchClock") Clock clock) {
        this.stocks = stocks;
        this.branches = branches;
        this.items = items;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Stock create(Stock stock) { return save(stock); }

    @Override
    @Transactional
    public Stock save(Stock request) {
        validate(request);
        if (request.getStockId() == null) {
            throw badRequest("stockId is required; Stock.stock_id is not auto-increment");
        }
        if (stocks.existsById(request.getStockId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock ID already exists: " + request.getStockId());
        }
        // A fresh entity always uses INSERT, including when the caller supplied an existing entity object.
        Stock stock = new Stock();
        stock.setStockId(request.getStockId());
        apply(stock, request);
        return persist(stock);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stock> getAll() { return stocks.findAll(); }

    @Override
    @Transactional(readOnly = true)
    public Stock getById(Integer stockId) {
        return stocks.findById(stockId).orElseThrow(() -> notFound("Stock", stockId));
    }

    @Override
    @Transactional
    public Stock update(Integer stockId, Stock request) {
        validate(request);
        if (request.getStockId() != null && !stockId.equals(request.getStockId())) {
            throw badRequest("Body stockId must match the URL stockId");
        }
        Stock stock = stocks.findByIdForUpdate(stockId).orElseThrow(() -> notFound("Stock", stockId));
        apply(stock, request);
        return persist(stock);
    }

    @Override
    @Transactional
    public void delete(Integer stockId) {
        Stock stock = stocks.findByIdForUpdate(stockId).orElseThrow(() -> notFound("Stock", stockId));
        try {
            // Hibernate removes only this batch's stock_items links, then the Stock row.
            stocks.delete(stock);
            stocks.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Stock cannot be deleted because another record references it");
        }
    }

    @Transactional(readOnly = true)
    public List<Stock> getByBranch(Integer branchId) {
        if (!branches.existsById(branchId)) throw notFound("Branch", branchId);
        return stocks.findByBranch_IdOrderByStockIdAsc(branchId);
    }

    @Transactional(readOnly = true)
    public List<Stock> getByItem(Integer itemId) {
        if (!items.existsById(itemId)) throw notFound("Inventory item", itemId);
        return stocks.findDistinctByItems_IdOrderByStockIdAsc(itemId);
    }

    @Transactional(readOnly = true)
    public List<Stock> getExpiring(int days) {
        if (days < 0) throw badRequest("days must be non-negative");
        LocalDate today = LocalDate.now(clock);
        LocalDate end;
        try {
            end = today.plusDays(days);
        } catch (DateTimeException | ArithmeticException exception) {
            throw badRequest("days is outside the supported date range");
        }
        // MySQL DATE supports years up to 9999.
        if (end.getYear() > 9999) throw badRequest("days is outside the supported date range");
        return stocks.findByExpiryDateBetweenOrderByExpiryDateAscStockIdAsc(today, end);
    }

    @Transactional(readOnly = true)
    public List<Stock> getExpired() {
        return stocks.findByExpiryDateBeforeOrderByExpiryDateAscStockIdAsc(LocalDate.now(clock));
    }

    private void validate(Stock stock) {
        if (stock == null) throw badRequest("Stock body is required");
        if (stock.getQuantity() == null || !Double.isFinite(stock.getQuantity()) || stock.getQuantity() < 0) {
            throw badRequest("quantity must be finite and non-negative");
        }
        if (stock.getBranchId() == null) throw badRequest("branchId is required");
        for (LocalDate date : new LocalDate[]{stock.getManufactureDate(), stock.getExpiryDate()}) {
            if (date != null && (date.getYear() < 1000 || date.getYear() > 9999)) {
                throw badRequest("Dates must be within the MySQL DATE range (1000 through 9999)");
            }
        }
        if (stock.getExpiryDate() != null && stock.getManufactureDate() != null
                && stock.getExpiryDate().isBefore(stock.getManufactureDate())) {
            throw badRequest("expiryDate must not be earlier than manufactureDate");
        }
        if (stock.getItemIds() == null || stock.getItemIds().contains(null)) {
            throw badRequest("itemIds must be an array of non-null inventory item IDs");
        }
    }

    private void apply(Stock stock, Stock request) {
        var branch = branches.findById(request.getBranchId()).orElseThrow(() -> badRequest("branchId does not reference an existing Branch"));
        Set<Integer> itemIds = request.getItemIds();
        List<InventoryItem> associatedItems = items.findAllById(itemIds);
        if (associatedItems.size() != itemIds.size()) {
            throw badRequest("Every itemId must reference an existing InventoryItem");
        }
        stock.setQuantity(request.getQuantity());
        stock.setManufactureDate(request.getManufactureDate());
        stock.setExpiryDate(request.getExpiryDate());
        stock.setBranch(branch);
        // Replace the relationship as a whole, including an explicit empty list.
        // There is no remove cascade or orphan removal on InventoryItem.
        stock.setItems(new LinkedHashSet<>(associatedItems));
    }

    private Stock persist(Stock stock) {
        try {
            return stocks.saveAndFlush(stock);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Stock could not be saved: its ID already exists or a referenced record changed");
        }
    }

    private ResponseStatusException badRequest(String reason) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
    }

    private ResponseStatusException notFound(String entity, Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, entity + " was not found: " + id);
    }

}
