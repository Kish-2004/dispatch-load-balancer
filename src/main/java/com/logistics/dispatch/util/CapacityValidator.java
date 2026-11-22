package com.logistics.dispatch.util;


import com.logistics.dispatch.entity.DeliveryOrder;
import com.logistics.dispatch.entity.Vehicle;

import java.math.BigDecimal;

public final class CapacityValidator {

    private CapacityValidator() {
    }

    public static boolean canFit(
            Vehicle vehicle,
            BigDecimal currentLoad,
            DeliveryOrder order
    ) {
        if (vehicle == null || currentLoad == null || order == null) {
            return false;
        }

        BigDecimal newLoad = currentLoad.add(order.getPackageWeight());

        return newLoad.compareTo(vehicle.getCapacity()) <= 0;
    }

    public static boolean exceedsCapacity(
            Vehicle vehicle,
            DeliveryOrder order
    ) {
        if (vehicle == null || order == null) {
            return true;
        }

        return order.getPackageWeight()
                .compareTo(vehicle.getCapacity()) > 0;
    }
}
