package com.example.stockmanagementsystembackend.domain.stock;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.repository.InventoryItemRepository;
import com.example.stockmanagementsystembackend.domain.organization.entity.Branch;
import com.example.stockmanagementsystembackend.domain.organization.repository.BranchRepository;
import com.example.stockmanagementsystembackend.domain.stock.entity.BranchTransferItem;
import com.example.stockmanagementsystembackend.domain.stock.entity.Branchtransferrequest;
import com.example.stockmanagementsystembackend.domain.stock.repository.StockTransferRepository;
import com.example.stockmanagementsystembackend.domain.stock.repository.StockTransferRequestItemRepository;
import com.example.stockmanagementsystembackend.domain.stock.service.StockMovementService;
import com.example.stockmanagementsystembackend.domain.stock.service.StockTransferService;
import com.example.stockmanagementsystembackend.user.entity.Status;
import com.example.stockmanagementsystembackend.user.entity.User;
import com.example.stockmanagementsystembackend.user.repository.StatusRepository;
import com.example.stockmanagementsystembackend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StockTransferServiceTest {
    private StockTransferRepository transfers;
    private StockTransferRequestItemRepository transferItems;
    private StockMovementService movements;
    private StatusRepository statuses;
    private StockTransferService service;
    private Branchtransferrequest transfer;
    private BranchTransferItem line;
    private Status approved;

    @BeforeEach
    void setUp() {
        transfers = mock(StockTransferRepository.class);
        transferItems = mock(StockTransferRequestItemRepository.class);
        BranchRepository branches = mock(BranchRepository.class);
        InventoryItemRepository inventory = mock(InventoryItemRepository.class);
        UserRepository users = mock(UserRepository.class);
        statuses = mock(StatusRepository.class);
        movements = mock(StockMovementService.class);
        service = new StockTransferService(transfers, transferItems, branches, inventory, users, statuses, movements);

        Branch source = branch(1, "Colombo");
        Branch destination = branch(2, "Kandy");
        User requester = new User();
        requester.setId(3);
        Status pending = new Status();
        pending.setId(4);
        pending.setName("Pending");
        approved = new Status();
        approved.setId(5);
        approved.setName("Approved");
        transfer = new Branchtransferrequest();
        transfer.setId(77);
        transfer.setSourceBranch(source);
        transfer.setDestinationBranch(destination);
        transfer.setRequestedByUser(requester);
        transfer.setStatus(pending);
        transfer.setRequestedAt(Instant.parse("2026-10-08T10:00:00Z"));

        InventoryItem item = new InventoryItem();
        item.setId(11);
        item.setItemName("Rice");
        line = new BranchTransferItem();
        line.setTransfer(transfer);
        line.setItem(item);
        line.setQuantity(6.0);
        when(transfers.findByIdForUpdate(77)).thenReturn(Optional.of(transfer));
        when(transfers.save(any(Branchtransferrequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transferItems.findByIdTransferId(77)).thenReturn(List.of(line));
        when(statuses.findByNameIgnoreCase("Approved")).thenReturn(Optional.of(approved));
        when(users.existsById(8)).thenReturn(true);
    }

    @Test
    void approvalTransfersStockAndSetsProcessedStatus() {
        var result = service.approveTransfer(77, 8);

        assertEquals("Approved", result.statusName());
        verify(movements).transfer(11, 1, 2, 6.0, 8, 77);
        verify(transfers).save(transfer);
    }

    private Branch branch(int id, String name) {
        Branch branch = new Branch();
        branch.setId(id);
        branch.setName(name);
        return branch;
    }
}
