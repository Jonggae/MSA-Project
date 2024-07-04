package com.jonggae.yakku.order.repository;

import com.jonggae.yakku.order.entity.Order;
import com.jonggae.yakku.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
//    Optional<Order> findByCustomerId(Long customerId);
    List<Order> findByCustomerId(Long customerId);
    Optional<Order> findPendingOrderByCustomerId(Long customerId);

    List<Order> findAllByCustomerId(Long customerId);

    Optional<Order> findByCustomerIdAndOrderStatus(Long customerId, OrderStatus orderStatus);
    Optional<Order> findByIdAndCustomerId(Long orderId, Long customerId);


}
