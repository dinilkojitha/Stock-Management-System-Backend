package com.example.stockmanagementsystembackend.domain.procurement.service;

import com.example.stockmanagementsystembackend.Core.EmailService;
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
import com.example.stockmanagementsystembackend.domain.stock.dto.request.StockReceiptRequest;
import com.example.stockmanagementsystembackend.domain.stock.service.StockMovementService;
import com.example.stockmanagementsystembackend.user.entity.User;
import com.example.stockmanagementsystembackend.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OrderService {
	private final OrderRepository orderRepository;
	private final PurchaseOrderItemRepository itemRepository;
	private final SupplierRepository supplierRepository;
	private final InventoryItemRepository inventoryItemRepository;
	private final UserRepository userRepository;
	private final StockMovementService stockMovementService;

	private static final Logger log = LoggerFactory.getLogger(SupplierService.class);
	private final EmailService emailService;


	public OrderService(OrderRepository orderRepository, PurchaseOrderItemRepository itemRepository,
                        SupplierRepository supplierRepository, InventoryItemRepository inventoryItemRepository,
                        UserRepository userRepository, EmailService emailService,
                        StockMovementService stockMovementService) {
		this.orderRepository = orderRepository;
		this.itemRepository = itemRepository;
		this.supplierRepository = supplierRepository;
		this.inventoryItemRepository = inventoryItemRepository;
		this.userRepository = userRepository;
        this.emailService = emailService;
		this.stockMovementService = stockMovementService;
    }


//	public Order create(OrderRequest request) {
//		Order order = new Order();
//		Supplier supplier = supplierRepository.findById(request.getSupplierId())
//				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found: " + request.getSupplierId()));
//
//		User user = userRepository.findById(request.getCreatedByUserId())
//				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + request.getCreatedByUserId()));
//		order.setSupplier(supplier);
//		order.setCreatedByUser(user);
//
//		order.setOrderDate(Instant.now());
//		order.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
//		order.setStatus("PENDING");
//
//		if(request.getItems() == null || request.getItems().isEmpty()) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain at least one item");
//		}else {
//			for(OrderItemRequest i : request.getItems()) {
//				int tofone = i.getItemId();
//				Optional<InventoryItem> item = inventoryItemRepository.findById(tofone);
//				if (!item.isPresent()) {
//					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory item not found: " + tofone);
//				}else {
//					double price = item.get().getUnitPrice() * i.getQuantity();
//					order.setTotalCost(order.getTotalCost() + price);
//				}
//
//			}
//		}
//		return orderRepository.save(order);
//
//	}

	@Transactional
	public OrderResponse create(OrderRequest request) {
		Order order = new Order();
		Supplier supplier = supplierRepository.findById(request.getSupplierId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found: " + request.getSupplierId()));

		User user = userRepository.findById(request.getCreatedByUserId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + request.getCreatedByUserId()));

		order.setSupplier(supplier);
		order.setCreatedByUser(user);
		order.setOrderDate(Instant.now());
		order.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
		order.setStatus("PENDING");
		if (request.getItems() == null || request.getItems().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain at least one item");
		}

		double totalCost = 0.0;
		Set<Integer> itemIds = new HashSet<>();
		Map<Integer, InventoryItem> itemsById = new HashMap<>();

		for (OrderItemRequest i : request.getItems()) {
			if (i.getItemId() == null || i.getQuantity() == null || !Double.isFinite(i.getQuantity())
					|| i.getQuantity() <= 0 || i.getUnitCost() == null || !Double.isFinite(i.getUnitCost())
					|| i.getUnitCost() <= 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order items require positive quantities and unit costs");
			}
			if (!itemIds.add(i.getItemId())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An item can only appear once in an order");
			}
			int tofone = i.getItemId();
			InventoryItem item = inventoryItemRepository.findById(tofone)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory item not found: " + tofone));
			itemsById.put(item.getId(), item);

			double price = i.getUnitCost() * i.getQuantity();
			totalCost += price;
		}

		order.setTotalCost(totalCost);
		Order savedOrder = orderRepository.save(order);

		for (OrderItemRequest i : request.getItems()) {
			InventoryItem item = itemsById.get(i.getItemId());
			PurchaseOrderItemId itemId = new PurchaseOrderItemId();
			itemId.setPurchaseOrderId(savedOrder.getId());
			itemId.setItemId(item.getId());

			PurchaseOrderItem orderItem = new PurchaseOrderItem();
			orderItem.setId(itemId);
			orderItem.setPurchaseOrder(savedOrder);
			orderItem.setItem(item);
			orderItem.setQuantity(i.getQuantity());
			orderItem.setUnitCost(i.getUnitCost());
			itemRepository.save(orderItem);
		}

		String orderUrl = urlGenarater(supplier.getId());
		triggerOrder(supplier.getEmail(), supplier.getContactPerson(), orderUrl);
		return response(savedOrder);
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
		String status = request.getStatus().trim().toUpperCase();
		if (!List.of("PENDING", "SHIPPED", "DELAYED").contains(status)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Delivery status can only be PENDING, SHIPPED, or DELAYED; record received stock to complete an order");
		}
		if (List.of("DELIVERED", "PARTIALLY_RECEIVED").contains(order.getStatus())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Received orders must be completed through the stock receipt workflow");
		}
		order.setStatus(status);
		return response(orderRepository.save(order));
	}

	@Transactional
	public OrderResponse receive(Integer id, OrderReceiptRequest request) {
		if (request == null || request.branchId() == null || request.branchId() <= 0
				|| request.receivedByUserId() == null || request.receivedByUserId() <= 0
				|| request.items() == null || request.items().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Branch, receiving user, and at least one receipt line are required");
		}

		Order order = orderRepository.findByIdForUpdate(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Purchase order not found: " + id));
		if ("DELIVERED".equalsIgnoreCase(order.getStatus())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Purchase order has already been fully received");
		}

		List<PurchaseOrderItem> orderItems = itemRepository.findByPurchaseOrderIdForUpdate(id);
		Map<Integer, PurchaseOrderItem> itemsById = new HashMap<>();
		for (PurchaseOrderItem orderItem : orderItems) itemsById.put(orderItem.getItem().getId(), orderItem);

		Set<Integer> receivedItemIds = new HashSet<>();
		Set<Integer> stockIds = new HashSet<>();
		for (OrderReceiptItemRequest receipt : request.items()) {
			if (receipt == null || receipt.itemId() == null || receipt.quantity() == null
					|| !Double.isFinite(receipt.quantity()) || receipt.quantity() <= 0
					|| receipt.stockId() == null || receipt.stockId() <= 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Each receipt line requires an item, batch ID, and positive finite quantity");
			}
			if (!receivedItemIds.add(receipt.itemId())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"An item can only be received once per submission");
			}
			if (!stockIds.add(receipt.stockId())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Each receipt line requires a unique batch ID");
			}
			PurchaseOrderItem orderItem = itemsById.get(receipt.itemId());
			if (orderItem == null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Item " + receipt.itemId() + " is not part of this purchase order");
			}
			double alreadyReceived = orderItem.getReceivedQuantity() == null ? 0 : orderItem.getReceivedQuantity();
			if (alreadyReceived + receipt.quantity() > orderItem.getQuantity()) {
				throw new ResponseStatusException(HttpStatus.CONFLICT,
						"Receipt exceeds the remaining quantity for " + orderItem.getItem().getItemName());
			}
		}

		for (OrderReceiptItemRequest receipt : request.items()) {
			PurchaseOrderItem orderItem = itemsById.get(receipt.itemId());
			stockMovementService.receive(new StockReceiptRequest(
					receipt.stockId(),
					receipt.itemId(),
					request.branchId(),
					receipt.quantity(),
					request.receivedByUserId(),
					receipt.manufactureDate(),
					receipt.expiryDate(),
					"Purchase order #" + id + " receipt"
			));
			double alreadyReceived = orderItem.getReceivedQuantity() == null ? 0 : orderItem.getReceivedQuantity();
			orderItem.setReceivedQuantity(alreadyReceived + receipt.quantity());
			itemRepository.save(orderItem);
		}

		boolean fullyReceived = orderItems.stream().allMatch(item ->
				(item.getReceivedQuantity() == null ? 0 : item.getReceivedQuantity()) >= item.getQuantity());
		if (fullyReceived) {
			order.setStatus("DELIVERED");
			order.setActualDeliveryDate(request.actualDeliveryDate() == null
					? LocalDate.now() : request.actualDeliveryDate());
		} else {
			order.setStatus("PARTIALLY_RECEIVED");
			order.setActualDeliveryDate(null);
		}
		orderRepository.save(order);
		return response(order);
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
						item.getQuantity(), item.getReceivedQuantity() == null ? 0 : item.getReceivedQuantity(),
						item.getUnitCost())).toList();
		return new OrderResponse(order.getId(), order.getSupplier().getId(), order.getSupplier().getCompanyName(),
				order.getCreatedByUser().getId(), order.getOrderDate(), order.getExpectedDeliveryDate(),
				order.getActualDeliveryDate(), order.getTotalCost(), order.getStatus(), items);
	}


	public void triggerOrder(String supplierEmail, String contactPerson, String orderUrl) {

		log.info("Attempting to send email to: {}", supplierEmail);
		try {
			emailService.sendOrderConfirmationEmail(supplierEmail, contactPerson, orderUrl);
			log.info("SUCCESS: Email sent successfully to {}", supplierEmail);
		} catch (MessagingException e) {
			log.error("ERROR: Failed to send email to {}. Cause: {}", supplierEmail, e.getMessage(), e);
		} catch (Exception e) {
			log.error("UNEXPECTED ERROR: Could not dispatch mail.", e);
		}
	}

	public String urlGenarater(int supplierId) {
		return "http://localhost:4200/order/" + supplierId;
	}


}
