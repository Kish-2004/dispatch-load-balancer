
package com.logistics.dispatch.controller;

import com.logistics.dispatch.dto.request.OrderBatchRequest;
import com.logistics.dispatch.dto.response.ApiResponse;
import com.logistics.dispatch.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dispatch/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createOrders(
            @Valid @RequestBody OrderBatchRequest request) {

        orderService.createOrders(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Delivery orders accepted."));
    }
}
