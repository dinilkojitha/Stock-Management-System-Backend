package com.example.stockmanagementsystembackend.domain.procurement.service;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.repository.InventoryItemRepository;
import com.example.stockmanagementsystembackend.domain.procurement.dto.*;
import com.example.stockmanagementsystembackend.domain.procurement.entity.Order;
import com.example.stockmanagementsystembackend.domain.procurement.entity.PurchaseOrderItem;
import com.example.stockmanagementsystembackend.domain.procurement.entity.PurchaseOrderItemId;
import com.example.stockmanagementsystembackend.domain.procurement.entity.Supplier;
import com.example.stockmanagementsystembackend.domain.procurement.repository.OrderRepository;
import com.example.stockmanagementsystembackend.domain.procurement.repository.PurchaseOrderItemRepository;
import com.example.stockmanagementsystembackend.domain.procurement.repository.SupplierRepository;
import com.example.stockmanagementsystembackend.user.entity.User;
import com.example.stockmanagementsystembackend.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class OrderService {
	private final OrderRepository orderRepository;
	private final PurchaseOrderItemRepository itemRepository;
	private final SupplierRepository supplierRepository;
	private final InventoryItemRepository inventoryItemRepository;
	private final UserRepository userRepository;

	public OrderService(OrderRepository orderRepository, PurchaseOrderItemRepository itemRepository,
						SupplierRepository supplierRepository, InventoryItemRepository inventoryItemRepository,
						UserRepository userRepository) {
		this.orderRepository = orderRepository;
		this.itemRepository = itemRepository;
		this.supplierRepository = supplierRepository;
		this.inventoryItemRepository = inventoryItemRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public OrderResponse create(OrderRequest request) {
		Order order = new Order();
		order.setSupplier(findSupplier(request.getSupplierId()));
		order.setCreatedByUser(findUser(request.getCreatedByUserId()));
		order.setOrderDate(Instant.now());
		order.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
		order.setStatus("DRAFT");
		order.setTotalCost(request.getItems().stream()
				.mapToDouble(item -> item.getQuantity() * item.getUnitCost()).sum());
		order = orderRepository.save(order);

		for (OrderItemRequest requestItem : request.getItems()) {
			InventoryItem inventoryItem = findItem(requestItem.getItemId());
			PurchaseOrderItem item = new PurchaseOrderItem();
			PurchaseOrderItemId id = new PurchaseOrderItemId();
			id.setPurchaseOrderId(order.getId());
			id.setItemId(inventoryItem.getId());
			item.setId(id);
			item.setPurchaseOrder(order);
			item.setItem(inventoryItem);
			item.setQuantity(requestItem.getQuantity());
			item.setUnitCost(requestItem.getUnitCost());
			itemRepository.save(item);
		}
		return response(order);
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> findAll() {
		return orderRepository.findAll().stream().map(this::response).toList();
	}

	@Transactional(readOnly = true)
	public OrderResponse findById(Integer id) {
		return response(find(id));
	}

	@Transactional
	public OrderResponse updateDelivery(Integer id, DeliveryUpdateRequest request) {
		Order order = find(id);
		order.setStatus(request.getStatus().trim().toUpperCase());
		order.setActualDeliveryDate(request.getActualDeliveryDate());
		return response(orderRepository.save(order));
	}

	private Order find(Integer id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase order not found: " + id));
	}

	private Supplier findSupplier(Integer id) {
		return supplierRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found: " + id));
	}

	private User findUser(Integer id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
	}

	private InventoryItem findItem(Integer id) {
		return inventoryItemRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory item not found: " + id));
	}

	private OrderResponse response(Order order) {
		List<OrderItemResponse> items = itemRepository.findByPurchaseOrderId(order.getId()).stream()
				.map(item -> new OrderItemResponse(item.getItem().getId(), item.getItem().getItemName(),
						item.getQuantity(), item.getUnitCost())).toList();
		return new OrderResponse(order.getId(), order.getSupplier().getId(), order.getSupplier().getCompanyName(),
				order.getCreatedByUser().getId(), order.getOrderDate(), order.getExpectedDeliveryDate(),
				order.getActualDeliveryDate(), order.getTotalCost(), order.getStatus(), items);
	}

}
