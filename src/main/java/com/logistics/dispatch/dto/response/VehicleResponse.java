package com.logistics.dispatch.dto.response;
import java.math.BigDecimal;

public record VehicleResponse(
        String vehicleId,
        BigDecimal capacity,
        Double currentLatitude,
        Double currentLongitude,
        String currentAddress
) {
}
