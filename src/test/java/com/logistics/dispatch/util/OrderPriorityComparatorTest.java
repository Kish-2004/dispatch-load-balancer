package com.logistics.dispatch.util;


import com.logistics.dispatch.entity.DeliveryOrder;
import com.logistics.dispatch.entity.Priority;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderPriorityComparatorTest {

    private DeliveryOrder createOrder(
            String id, Priority priority, String weight) {

        DeliveryOrder order = new DeliveryOrder();
        order.setOrderId(id);
        order.setPriority(priority);
        order.setPackageWeight(new BigDecimal(weight));
        return order;
    }

    @Test
    void shouldSortHighPriorityBeforeMediumAndLow() {
        List<DeliveryOrder> orders = new ArrayList<>(List.of(
                createOrder("O3", Priority.LOW, "10"),
                createOrder("O2", Priority.MEDIUM, "10"),
                createOrder("O1", Priority.HIGH, "10")
        ));

        orders.sort(new OrderPriorityComparator());

        assertEquals("O1", orders.get(0).getOrderId());
        assertEquals("O2", orders.get(1).getOrderId());
        assertEquals("O3", orders.get(2).getOrderId());
    }

    @Test
    void shouldSortHeavierOrdersFirstWithinSamePriority() {
        List<DeliveryOrder> orders = new ArrayList<>(List.of(
                createOrder("O1", Priority.HIGH, "10"),
                createOrder("O2", Priority.HIGH, "30"),
                createOrder("O3", Priority.HIGH, "20")
        ));

        orders.sort(new OrderPriorityComparator());

        assertEquals("O2", orders.get(0).getOrderId());
        assertEquals("O3", orders.get(1).getOrderId());
        assertEquals("O1", orders.get(2).getOrderId());
    }

    @Test
    void shouldSortByOrderIdWhenPriorityAndWeightAreEqual() {
        List<DeliveryOrder> orders = new ArrayList<>(List.of(
                createOrder("O3", Priority.HIGH, "10"),
                createOrder("O1", Priority.HIGH, "10"),
                createOrder("O2", Priority.HIGH, "10")
        ));

        orders.sort(new OrderPriorityComparator());

        assertEquals("O1", orders.get(0).getOrderId());
        assertEquals("O2", orders.get(1).getOrderId());
        assertEquals("O3", orders.get(2).getOrderId());
    }
}