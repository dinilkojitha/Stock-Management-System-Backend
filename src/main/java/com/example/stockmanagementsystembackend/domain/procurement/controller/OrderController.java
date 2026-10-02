package com.example.stockmanagementsystembackend.domain.procurement.controller;

import com.example.stockmanagementsystembackend.domain.procurement.dto.DeliveryUpdateRequest;
import com.example.stockmanagementsystembackend.domain.procurement.dto.OrderRequest;
import com.example.stockmanagementsystembackend.domain.procurement.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/orders")
public class OrderController {
	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	public Object create(@Valid @RequestBody OrderRequest request) {
		return orderService.create(request);
	}

	@GetMapping
	public Object findAll() {
		return orderService.findAll();
	}

	@GetMapping("/{id}")
	public Object findById(@PathVariable Integer id) {
		return orderService.findById(id);
	}

	@PatchMapping("/{id}/delivery")
	public Object updateDelivery(@PathVariable Integer id, @Valid @RequestBody DeliveryUpdateRequest request) {
		return orderService.updateDelivery(id, request);
	}

}
