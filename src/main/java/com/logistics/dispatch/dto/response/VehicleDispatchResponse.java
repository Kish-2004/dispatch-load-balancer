package com.logistics.dispatch.dto.response;


import java.math.BigDecimal;
import java.util.List;

public record VehicleDispatchResponse(
        String vehicleId,
        BigDecimal totalLoad,
        String totalDistance,
        List<OrderResponse> assignedOrders
) {
}
