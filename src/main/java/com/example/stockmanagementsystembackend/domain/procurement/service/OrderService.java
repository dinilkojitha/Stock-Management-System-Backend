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
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
	private final OrderRepository orderRepository;
	private final PurchaseOrderItemRepository itemRepository;
	private final SupplierRepository supplierRepository;
	private final InventoryItemRepository inventoryItemRepository;
	private final UserRepository userRepository;

	private static final Logger log = LoggerFactory.getLogger(SupplierService.class);
	private final EmailService emailService;


	public OrderService(OrderRepository orderRepository, PurchaseOrderItemRepository itemRepository,
                        SupplierRepository supplierRepository, InventoryItemRepository inventoryItemRepository,
                        UserRepository userRepository, EmailService emailService) {
		this.orderRepository = orderRepository;
		this.itemRepository = itemRepository;
		this.supplierRepository = supplierRepository;
		this.inventoryItemRepository = inventoryItemRepository;
		this.userRepository = userRepository;
        this.emailService = emailService;
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

	public Order create(OrderRequest request) {
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
		String orderUrl = urlGenarater(supplier.getId());
		triggerOrder(supplier.getEmail(), supplier.getContactPerson(), orderUrl);
		if (request.getItems() == null || request.getItems().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain at least one item");
		}

		double totalCost = 0.0; // Track the running sum locally

		for (OrderItemRequest i : request.getItems()) {
			int tofone = i.getItemId();
			InventoryItem item = inventoryItemRepository.findById(tofone)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory item not found: " + tofone));

			double price = item.getUnitPrice() * i.getQuantity();
			totalCost += price;
		}

		order.setTotalCost(totalCost); // Set once after the loop

		return orderRepository.save(order);
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
