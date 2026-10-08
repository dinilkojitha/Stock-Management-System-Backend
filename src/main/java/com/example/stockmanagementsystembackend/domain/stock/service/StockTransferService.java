package com.example.stockmanagementsystembackend.domain.stock.service;

import com.example.stockmanagementsystembackend.domain.organization.entity.Branch;
import com.example.stockmanagementsystembackend.domain.organization.repository.BranchRepository;
import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.repository.InventoryItemRepository;
import com.example.stockmanagementsystembackend.domain.stock.dto.request.*;
import com.example.stockmanagementsystembackend.domain.stock.dto.response.*;
import com.example.stockmanagementsystembackend.domain.stock.entity.*;
import com.example.stockmanagementsystembackend.domain.stock.repository.*;
import com.example.stockmanagementsystembackend.user.entity.*;
import com.example.stockmanagementsystembackend.user.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StockTransferService {
	private final StockTransferRepository transferRepository;
	private final StockTransferRequestItemRepository itemRepository;
	private final BranchRepository branchRepository;
	private final InventoryItemRepository inventoryRepository;
	private final UserRepository userRepository;
	private final StatusRepository statusRepository;
	private final StockMovementService stockMovementService;

	public StockTransferService(StockTransferRepository transferRepository,
			StockTransferRequestItemRepository itemRepository,
			BranchRepository branchRepository, InventoryItemRepository inventoryRepository,
			UserRepository userRepository, StatusRepository statusRepository,
			StockMovementService stockMovementService) {
		this.transferRepository = transferRepository;
		this.itemRepository = itemRepository;
		this.branchRepository = branchRepository;
		this.inventoryRepository = inventoryRepository;
		this.userRepository = userRepository;
		this.statusRepository = statusRepository;
		this.stockMovementService = stockMovementService;
	}

	@Transactional
	public StockTransferResponse createTransferRequest(StockTransferCreateRequest request) {
		Branch from = branch(request.getFromBranchId());
		Branch to = branch(request.getToBranchId());
		if (from.getId().equals(to.getId()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer branches must be different");
		User user = userRepository.findById(request.getRequestedById())
				.orElseThrow(() -> notFound("User", request.getRequestedById()));
		Status status = statusRepository.findByNameIgnoreCase("Pending").orElseGet(() -> {
			Status pending = new Status();
			pending.setName("Pending");
			return statusRepository.save(pending);
		});
		Branchtransferrequest transfer = new Branchtransferrequest();
		transfer.setSourceBranch(from);
		transfer.setDestinationBranch(to);
		transfer.setRequestedByUser(user);
		transfer.setStatus(status);
		transfer.setRequestedAt(Instant.now());
		if (request.getItems() == null || request.getItems().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A transfer must contain at least one item");
		}
		Set<Integer> itemIds = new HashSet<>();
		for (TransferItemDto dto : request.getItems()) {
			if (dto.getInventoryItemId() == null || dto.getQuantity() == null
					|| !Double.isFinite(dto.getQuantity()) || dto.getQuantity() <= 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Transfer items require an item and a positive finite quantity");
			}
			if (!itemIds.add(dto.getInventoryItemId())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"An item can only appear once in a transfer request");
			}
		}
		transfer = transferRepository.save(transfer);
		for (TransferItemDto dto : request.getItems().stream()
				.sorted(java.util.Comparator.comparing(TransferItemDto::getInventoryItemId)).toList()) {
			InventoryItem item = inventoryRepository.findById(dto.getInventoryItemId())
					.orElseThrow(() -> notFound("Inventory item", dto.getInventoryItemId()));
			BranchTransferItemId itemId = new BranchTransferItemId();
			itemId.setTransferId(transfer.getId());
			itemId.setItemId(item.getId());
			BranchTransferItem transferItem = new BranchTransferItem();
			transferItem.setId(itemId);
			transferItem.setTransfer(transfer);
			transferItem.setItem(item);
			transferItem.setQuantity(dto.getQuantity());
			itemRepository.save(transferItem);
		}
		return response(transfer);
	}

	@Transactional(readOnly = true)
	public List<StockTransferResponse> getAllTransfers() {
		return transferRepository.findAll().stream().map(this::response).toList();
	}

	@Transactional(readOnly = true)
	public StockTransferResponse getTransferById(Integer id) {
		return response(find(id));
	}

	@Transactional(readOnly = true)
	public List<StockTransferResponse> getTransfersByBranch(Integer id) {
		Branch branch = branch(id);
		return transferRepository.findByDestinationBranch(branch).stream().map(this::response).toList();
	}

	@Transactional
	public StockTransferResponse approveTransfer(Integer id, Integer approvedByUserId) {
		return process(id, true, approvedByUserId);
	}

	@Transactional
	public StockTransferResponse rejectTransfer(Integer id) {
		return process(id, false, null);
	}

	private StockTransferResponse process(Integer id, boolean approve, Integer approvedByUserId) {
		Branchtransferrequest transfer = transferRepository.findByIdForUpdate(id)
				.orElseThrow(() -> notFound("Transfer", id));
		String current = transfer.getStatus().getName();
		if (!"Pending".equalsIgnoreCase(current))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Transfer has already been processed");
		if (approve) {
			if (approvedByUserId == null || approvedByUserId <= 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An approving user is required");
			}
			if (!userRepository.existsById(approvedByUserId)) {
				throw notFound("User", approvedByUserId);
			}
			itemRepository.findByIdTransferId(id).stream()
					.sorted(java.util.Comparator.comparing(item -> item.getItem().getId()))
					.forEach(item -> stockMovementService.transfer(
							item.getItem().getId(),
							transfer.getSourceBranch().getId(),
							transfer.getDestinationBranch().getId(),
							item.getQuantity(),
							approvedByUserId,
							id
					));
		}
		String target = approve ? "Approved" : "Rejected";
		Status status = statusRepository.findByNameIgnoreCase(target).orElseGet(() -> {
			Status created = new Status();
			created.setName(target);
			return statusRepository.save(created);
		});
		transfer.setStatus(status);
		return response(transferRepository.save(transfer));
	}

	private Branchtransferrequest find(Integer id) {
		return transferRepository.findById(id).orElseThrow(() -> notFound("Transfer", id));
	}

	private Branch branch(Integer id) {
		return branchRepository.findById(id).orElseThrow(() -> notFound("Branch", id));
	}

	private ResponseStatusException notFound(String type, Integer id) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, type + " not found: " + id);
	}

	private StockTransferResponse response(Branchtransferrequest transfer) {
		Branch from = transfer.getSourceBranch();
		Integer fromBranchId = from.getId();
		String fromBranchName = from.getName();
		Branch to = transfer.getDestinationBranch();
		User user = transfer.getRequestedByUser();
		Status status = transfer.getStatus();
		List<TransferItemResponseDto> items = itemRepository.findByIdTransferId(transfer.getId())
				.stream().map(item -> new TransferItemResponseDto(item.getItem().getId(),
						item.getItem().getItemName(), item.getQuantity()))
				.toList();
		return new StockTransferResponse(transfer.getId(), fromBranchId, fromBranchName, to.getId(),
				to.getName(), user.getId(), user.getFullName(), status.getId(), status.getName(),
				transfer.getRequestedAt().toString(), items);
	}

}
