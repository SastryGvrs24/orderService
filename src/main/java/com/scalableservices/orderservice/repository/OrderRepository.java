package com.scalableservices.orderservice.repository;

import com.scalableservices.orderservice.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByIdAndRestaurantId(long id, long restaurantId);
}
