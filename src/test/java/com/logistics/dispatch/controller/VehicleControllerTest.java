package com.logistics.dispatch.controller;


import com.logistics.dispatch.dto.request.VehicleBatchRequest;
import com.logistics.dispatch.dto.response.ApiResponse;
import com.logistics.dispatch.service.VehicleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

class VehicleControllerTest {

    private VehicleService vehicleService;
    private VehicleController vehicleController;

    @BeforeEach
    void setUp() {
        vehicleService = mock(VehicleService.class);
        vehicleController = new VehicleController(vehicleService);
    }

    @Test
    void shouldCreateVehiclesSuccessfully() {

        VehicleBatchRequest request = mock(VehicleBatchRequest.class);

        ResponseEntity<ApiResponse> response =
                vehicleController.createVehicles(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Vehicle details accepted.",
                response.getBody().message());
        assertEquals("success", response.getBody().status());

        verify(vehicleService, times(1)).createVehicles(request);
    }

    @Test
    void shouldPropagateServiceException() {

        VehicleBatchRequest request = mock(VehicleBatchRequest.class);

        doThrow(new RuntimeException("Database error"))
                .when(vehicleService).createVehicles(request);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> vehicleController.createVehicles(request)
        );

        assertEquals("Database error", exception.getMessage());

        verify(vehicleService).createVehicles(request);
    }
}
