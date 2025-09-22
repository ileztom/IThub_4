package com.example.api.controller;

import com.example.api.model.OrderEntity;
import com.example.api.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderRepository orderRepository;
    public OrderController(OrderRepository orderRepository){this.orderRepository = orderRepository;}

    @PostMapping
    public OrderEntity create(@RequestBody OrderEntity order){
        return orderRepository.save(order);
    }

    @GetMapping("/user/{userId}")
    public List<OrderEntity> getByUser(@PathVariable Long userId){
        return orderRepository.findByUserId(userId);
    }
}
