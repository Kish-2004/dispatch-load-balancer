package com.logistics.dispatch.dto.response;

import com.logistics.dispatch.entity.Priority;

import java.math.BigDecimal;

public record OrderResponse(
        String orderId,
        Double latitude,
        Double longitude,
        String address,
        BigDecimal packageWeight,
        Priority priority
) {
}
