package com.logistics.dispatch.service;

import com.logistics.dispatch.dto.request.OrderBatchRequest;
import com.logistics.dispatch.dto.request.OrderRequest;
import com.logistics.dispatch.dto.response.OrderResponse;
import com.logistics.dispatch.entity.DeliveryOrder;
import com.logistics.dispatch.repository.OrderRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void createOrders(OrderBatchRequest request) {

        List<OrderRequest> orders = request.orders();

        // Check duplicate IDs within the incoming request.
        Set<String> incomingIds = new HashSet<>();

        for (OrderRequest order : orders) {
            String orderId = order.orderId().trim();

            if (!incomingIds.add(orderId)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Duplicate order ID in request: " + orderId
                );
            }
        }

        // Check whether any IDs already exist in the database.
        List<DeliveryOrder> existingOrders =
                orderRepository.findAllByOrderIdIn(incomingIds.stream().toList());

        if (!existingOrders.isEmpty()) {
            String duplicateId = existingOrders.get(0).getOrderId();

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Order already exists: " + duplicateId
            );
        }

        // Convert request DTOs into JPA entities.
        List<DeliveryOrder> entities = new ArrayList<>();

        for (OrderRequest order : orders) {
            DeliveryOrder entity = new DeliveryOrder();

            entity.setOrderId(order.orderId().trim());
            entity.setLatitude(order.latitude());
            entity.setLongitude(order.longitude());
            entity.setAddress(order.address().trim());
            entity.setPackageWeight(order.packageWeight());
            entity.setPriority(order.priority());

            entities.add(entity);
        }

        orderRepository.saveAll(entities);
    }

}
