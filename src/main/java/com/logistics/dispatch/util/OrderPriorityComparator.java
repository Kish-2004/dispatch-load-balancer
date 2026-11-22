package com.logistics.dispatch.util;


import com.logistics.dispatch.entity.DeliveryOrder;
import com.logistics.dispatch.entity.Priority;

import java.util.Comparator;
import java.util.Map;

public class OrderPriorityComparator implements Comparator<DeliveryOrder> {

    private static final Map<Priority, Integer> PRIORITY_RANK = Map.of(
            Priority.HIGH, 1,
            Priority.MEDIUM, 2,
            Priority.LOW, 3
    );

    @Override
    public int compare(DeliveryOrder first, DeliveryOrder second) {

        int priorityComparison = Integer.compare(
                PRIORITY_RANK.get(first.getPriority()),
                PRIORITY_RANK.get(second.getPriority())
        );

        if (priorityComparison != 0) {
            return priorityComparison;
        }

        // For equal priority, assign heavier orders first.
        int weightComparison = second.getPackageWeight()
                .compareTo(first.getPackageWeight());

        if (weightComparison != 0) {
            return weightComparison;
        }

        // Deterministic ordering.
        return first.getOrderId().compareTo(second.getOrderId());
    }
}