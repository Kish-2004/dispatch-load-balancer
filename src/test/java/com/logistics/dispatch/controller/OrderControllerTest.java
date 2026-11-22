package com.logistics.dispatch.controller;


import com.logistics.dispatch.dto.request.OrderBatchRequest;
import com.logistics.dispatch.dto.response.ApiResponse;
import com.logistics.dispatch.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

class OrderControllerTest {

    private OrderService orderService;
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        orderController = new OrderController(orderService);
    }

    @Test
    void shouldCreateOrdersSuccessfully() {

        OrderBatchRequest request = mock(OrderBatchRequest.class);

        ResponseEntity<ApiResponse> response =
                orderController.createOrders(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Delivery orders accepted.",
                response.getBody().message());
        assertEquals("success", response.getBody().status());

        verify(orderService, times(1)).createOrders(request);
    }

    @Test
    void shouldPropagateServiceException() {

        OrderBatchRequest request = mock(OrderBatchRequest.class);

        doThrow(new RuntimeException("Database error"))
                .when(orderService).createOrders(request);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderController.createOrders(request)
        );

        assertEquals("Database error", exception.getMessage());

        verify(orderService).createOrders(request);
    }
}
