package com.logistics.dispatch.repository;

import com.logistics.dispatch.entity.DeliveryOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<DeliveryOrder, Long> {

    Optional<DeliveryOrder> findByOrderId(String orderId);

    boolean existsByOrderId(String orderId);

    List<DeliveryOrder> findAllByOrderIdIn(Collection<String> orderIds);

    List<DeliveryOrder> findAllByOrderByOrderIdAsc();
}
