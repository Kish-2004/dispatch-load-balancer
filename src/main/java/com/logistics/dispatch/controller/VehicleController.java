
package com.logistics.dispatch.controller;

import com.logistics.dispatch.dto.request.VehicleBatchRequest;
import com.logistics.dispatch.dto.response.ApiResponse;
import com.logistics.dispatch.service.VehicleService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dispatch/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createVehicles(
            @Valid @RequestBody VehicleBatchRequest request) {

        vehicleService.createVehicles(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vehicle details accepted."));
    }
}
