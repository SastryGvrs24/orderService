package com.scalableservices.orderservice.controller;

import com.scalableservices.orderservice.domain.Orders;
import com.scalableservices.orderservice.dto.ORDERSTATUS;
import com.scalableservices.orderservice.dto.OrderDTO;
import com.scalableservices.orderservice.dto.UpdateOrderDTO;
import com.scalableservices.orderservice.repository.OrderRepository;
import com.scalableservices.orderservice.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.function.Consumer;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('customer')")
    public ResponseEntity createOrder(@RequestBody Orders order, HttpServletRequest request) {
        order.setStatus(ORDERSTATUS.CREATED);
        orderService.createOrder(order, request);
        return ResponseEntity.ok("{ \"msg\" :Order has been created Successfully}");
    }

    @PostMapping("/{id}/updateOrder")
    @PreAuthorize("hasRole('customer')")
    public ResponseEntity updateOrder(@PathVariable Long id, @RequestBody UpdateOrderDTO order) {
        Optional<Orders> orders= orderRepository.findById(id);

        if((orders.isPresent())) {
            orders.get().setStatus(ORDERSTATUS.valueOf(order.getOrderstatus()));
            orderRepository.save(orders.get());
            return ResponseEntity.ok("Order updated Sucessfully by Customer");
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('restaurant')")
    @PostMapping("/updateOrderRestaurant")
    public ResponseEntity updateOrderByRestaurant(@RequestBody UpdateOrderDTO order) {
        Optional<Orders> orders= orderRepository.findByIdAndRestaurantId(order.getId(), order.getRestaurantId());

        if((orders.isPresent())) {
            updateNonNullAttributes(ORDERSTATUS.valueOf(order.getOrderstatus()), orders.get()::setStatus);
            orderRepository.save(orders.get());
            return ResponseEntity.ok("Order updated Sucessfully from order Service by restaurant");
        }

        return ResponseEntity.notFound().build();
    }


    public static <T> void updateNonNullAttributes(T value, Consumer<T> function) {
        if(value != null) {
            function.accept(value);
        }
    }

    // Get Order by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('customer', 'restaurant')")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id, HttpServletRequest request) {
        OrderDTO order = orderService.getOrderById(id, request);
        if(order != null) {
            return ResponseEntity.ok(order);
        }

        return ResponseEntity.notFound().build();
    }

}
