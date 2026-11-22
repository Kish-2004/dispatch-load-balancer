package com.logistics.dispatch.dto.request;

import com.logistics.dispatch.entity.Priority;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record OrderRequest(
        @NotBlank(message = "orderId is required")
        @Size(max = 50, message = "orderId must not exceed 50 characters")
        String orderId,

        @NotNull(message = "latitude is required")
        @DecimalMin(value = "-90.0", message = "latitude must be at least -90")
        @DecimalMax(value = "90.0", message = "latitude must not exceed 90")
        Double latitude,

        @NotNull(message = "longitude is required")
        @DecimalMin(value = "-180.0", message = "longitude must be at least -180")
        @DecimalMax(value = "180.0", message = "longitude must not exceed 180")
        Double longitude,

        @NotBlank(message = "address is required")
        @Size(max = 255, message = "address must not exceed 255 characters")
        String address,

        @NotNull(message = "packageWeight is required")
        @DecimalMin(value = "0.001", message = "packageWeight must be greater than zero")
        @Digits(integer = 9, fraction = 3, message = "packageWeight supports up to 3 decimal places")
        BigDecimal packageWeight,

        @NotNull(message = "priority is required")
        Priority priority
) {
}
