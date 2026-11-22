package com.logistics.dispatch.dto.response;

import java.util.List;

public record DispatchPlanResponse(
        List<VehicleDispatchResponse> dispatchPlan,
        List<OrderResponse> unassignedOrders
) {
}