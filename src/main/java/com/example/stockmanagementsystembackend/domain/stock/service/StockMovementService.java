package com.example.stockmanagementsystembackend.domain.stock.service;

import com.example.stockmanagementsystembackend.domain.audit.entity.Transaction;
import com.example.stockmanagementsystembackend.domain.audit.repository.TransactionRepository;
import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.repository.InventoryItemRepository;
import com.example.stockmanagementsystembackend.domain.organization.entity.Branch;
import com.example.stockmanagementsystembackend.domain.organization.repository.BranchRepository;
import com.example.stockmanagementsystembackend.domain.stock.dto.request.StockIssueRequest;
import com.example.stockmanagementsystembackend.domain.stock.dto.request.StockReceiptRequest;
import com.example.stockmanagementsystembackend.domain.stock.dto.response.StockMovementResponse;
import com.example.stockmanagementsystembackend.domain.stock.entity.Stock;
import com.example.stockmanagementsystembackend.domain.stock.repository.StockRepository;
import com.example.stockmanagementsystembackend.user.entity.User;
import com.example.stockmanagementsystembackend.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Validated
public class StockMovementService {
    private final StockService stockService;
    private final StockRepository stocks;
    private final InventoryItemRepository items;
    private final BranchRepository branches;
    private final UserRepository users;
    private final TransactionRepository transactions;
    private final Clock clock;

    public StockMovementService(
            StockService stockService,
            StockRepository stocks,
            InventoryItemRepository items,
            BranchRepository branches,
            UserRepository users,
            TransactionRepository transactions,
            @org.springframework.beans.factory.annotation.Qualifier("stockBatchClock") Clock clock
    ) {
        this.stockService = stockService;
        this.stocks = stocks;
        this.items = items;
        this.branches = branches;
        this.users = users;
        this.transactions = transactions;
        this.clock = clock;
    }

    @Transactional
    public StockMovementResponse receive(@Valid StockReceiptRequest request) {
        validateQuantity(request.quantity());
        InventoryItem item = lockItem(request.itemId());
        Branch branch = branches.findById(request.branchId())
                .orElseThrow(() -> notFound("Branch", request.branchId()));
        User user = findUser(request.userId());
        Integer stockId = request.stockId() == null ? generateStockId() : request.stockId();

        Stock batch = new Stock();
        batch.setStockId(stockId);
        batch.setQuantity(request.quantity());
        batch.setManufactureDate(request.manufactureDate());
        batch.setExpiryDate(request.expiryDate());
        batch.setBranch(branch);
        batch.setItems(java.util.Set.of(item));
        Stock savedBatch = stockService.save(batch);

        recordTransaction("STOCK_IN", user, item, branch, request.quantity(), request.remarks());
        return new StockMovementResponse(
                item.getId(),
                item.getItemName(),
                branch.getId(),
                request.quantity(),
                item.getTotalQuantity(),
                List.of(savedBatch.getStockId())
        );
    }

    @Transactional
    public StockMovementResponse issue(@Valid StockIssueRequest request) {
        validateQuantity(request.quantity());
        InventoryItem item = lockItem(request.itemId());
        Branch branch = branches.findById(request.branchId())
                .orElseThrow(() -> notFound("Branch", request.branchId()));
        User user = findUser(request.userId());
        LocalDate today = LocalDate.now(clock);

        List<Stock> availableBatches = new ArrayList<>(
                stocks.findDistinctByItems_IdAndBranch_Id(item.getId(), branch.getId())
        );
        availableBatches.sort(
                Comparator.comparing(Stock::getExpiryDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Stock::getStockId)
        );

        double remainingToIssue = request.quantity();
        List<Integer> affectedStockIds = new ArrayList<>();
        for (Stock candidate : availableBatches) {
            if (remainingToIssue <= 0) break;
            if (candidate.getQuantity() == null || candidate.getQuantity() <= 0) continue;
            if (candidate.getExpiryDate() != null && candidate.getExpiryDate().isBefore(today)) continue;

            Stock batch = stocks.findByIdForUpdate(candidate.getStockId())
                    .orElseThrow(() -> notFound("Stock batch", candidate.getStockId()));
            if (batch.getExpiryDate() != null && batch.getExpiryDate().isBefore(today)) continue;

            double issuedFromBatch = Math.min(batch.getQuantity(), remainingToIssue);
            batch.setQuantity(batch.getQuantity() - issuedFromBatch);
            remainingToIssue -= issuedFromBatch;
            affectedStockIds.add(batch.getStockId());
        }

        if (remainingToIssue > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Insufficient unexpired stock in " + branch.getName()
                            + ". Available: " + (request.quantity() - remainingToIssue)
            );
        }

        double totalQuantity = item.getTotalQuantity() == null ? 0 : item.getTotalQuantity();
        if (totalQuantity < request.quantity()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Inventory total is lower than the available batch quantity; reconcile the item before issuing"
            );
        }
        item.setTotalQuantity(totalQuantity - request.quantity());
        recordTransaction("STOCK_OUT", user, item, branch, -request.quantity(), request.remarks());

        return new StockMovementResponse(
                item.getId(),
                item.getItemName(),
                branch.getId(),
                request.quantity(),
                item.getTotalQuantity(),
                List.copyOf(affectedStockIds)
        );
    }

    @Transactional
    public void transfer(Integer itemId, Integer sourceBranchId, Integer destinationBranchId,
                         double quantity, Integer userId, Integer transferId) {
        validateQuantity(quantity);
        if (sourceBranchId.equals(destinationBranchId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer branches must be different");
        }
        InventoryItem item = lockItem(itemId);
        Branch source = branches.findById(sourceBranchId)
                .orElseThrow(() -> notFound("Branch", sourceBranchId));
        Branch destination = branches.findById(destinationBranchId)
                .orElseThrow(() -> notFound("Branch", destinationBranchId));
        User user = findUser(userId);
        LocalDate today = LocalDate.now(clock);
        List<Stock> candidates = new ArrayList<>(
                stocks.findDistinctByItems_IdAndBranch_Id(itemId, sourceBranchId)
        );
        candidates.sort(Comparator.comparing(Stock::getExpiryDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Stock::getStockId));

        double remaining = quantity;
        List<Stock> selectedBatches = new ArrayList<>();
        List<Double> selectedQuantities = new ArrayList<>();
        for (Stock candidate : candidates) {
            if (remaining <= 0) break;
            if (candidate.getQuantity() == null || candidate.getQuantity() <= 0
                    || (candidate.getExpiryDate() != null && candidate.getExpiryDate().isBefore(today))) {
                continue;
            }
            Stock batch = stocks.findByIdForUpdate(candidate.getStockId())
                    .orElseThrow(() -> notFound("Stock batch", candidate.getStockId()));
            if (!batch.getBranch().getId().equals(sourceBranchId)
                    || (batch.getExpiryDate() != null && batch.getExpiryDate().isBefore(today))) {
                continue;
            }
            double moved = Math.min(batch.getQuantity(), remaining);
            selectedBatches.add(batch);
            selectedQuantities.add(moved);
            remaining -= moved;
        }
        if (remaining > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Insufficient unexpired stock in " + source.getName()
                            + ". Available: " + (quantity - remaining));
        }

        for (int i = 0; i < selectedBatches.size(); i++) {
            Stock batch = selectedBatches.get(i);
            double moved = selectedQuantities.get(i);
            if (moved == batch.getQuantity()) {
                batch.setBranch(destination);
                stocks.save(batch);
            } else {
                batch.setQuantity(batch.getQuantity() - moved);
                Stock destinationBatch = new Stock();
                destinationBatch.setStockId(generateStockId());
                destinationBatch.setQuantity(moved);
                destinationBatch.setManufactureDate(batch.getManufactureDate());
                destinationBatch.setExpiryDate(batch.getExpiryDate());
                destinationBatch.setBranch(destination);
                destinationBatch.setItems(java.util.Set.of(item));
                stocks.save(destinationBatch);
            }
            recordTransaction("TRANSFER_OUT", user, item, source, -moved,
                    "Branch transfer #" + transferId + " to " + destination.getName());
            recordTransaction("TRANSFER_IN", user, item, destination, moved,
                    "Branch transfer #" + transferId + " from " + source.getName());
        }
    }

    private Integer generateStockId() {
        for (int attempt = 0; attempt < 10; attempt++) {
            int candidate = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
            if (!stocks.existsById(candidate)) return candidate;
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Could not allocate a unique stock batch ID; retry the transfer");
    }

    private InventoryItem lockItem(Integer itemId) {
        return items.findByIdForUpdate(itemId).orElseThrow(() -> notFound("Inventory item", itemId));
    }

    private User findUser(Integer userId) {
        return users.findById(userId).orElseThrow(() -> notFound("User", userId));
    }

    private void validateQuantity(Double quantity) {
        if (quantity == null || !Double.isFinite(quantity) || quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "quantity must be a positive finite number");
        }
    }

    private void recordTransaction(String type, User user, InventoryItem item, Branch branch,
                                   double quantity, String remarks) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(type);
        transaction.setUserUserid(user);
        transaction.setItem(item);
        transaction.setBranch(branch);
        transaction.setQuantityDelta(quantity);
        transaction.setRemarks(remarks);
        transaction.setTransactedAt(java.time.Instant.now(clock));
        transactions.save(transaction);
    }

    private ResponseStatusException notFound(String entity, Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, entity + " was not found: " + id);
    }
}
