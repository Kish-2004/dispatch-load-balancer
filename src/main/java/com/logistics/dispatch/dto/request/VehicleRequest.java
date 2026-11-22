package com.logistics.dispatch.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record VehicleRequest(
        @NotBlank(message = "vehicleId is required")
        @Size(max = 50, message = "vehicleId must not exceed 50 characters")
        String vehicleId,

        @NotNull(message = "capacity is required")
        @DecimalMin(value = "0.001", message = "capacity must be greater than zero")
        @Digits(integer = 9, fraction = 3, message = "capacity supports up to 3 decimal places")
        BigDecimal capacity,

        @NotNull(message = "currentLatitude is required")
        @DecimalMin(value = "-90.0", message = "currentLatitude must be at least -90")
        @DecimalMax(value = "90.0", message = "currentLatitude must not exceed 90")
        Double currentLatitude,

        @NotNull(message = "currentLongitude is required")
        @DecimalMin(value = "-180.0", message = "currentLongitude must be at least -180")
        @DecimalMax(value = "180.0", message = "currentLongitude must not exceed 180")
        Double currentLongitude,

        @NotBlank(message = "currentAddress is required")
        @Size(max = 255, message = "currentAddress must not exceed 255 characters")
        String currentAddress
) {
}
