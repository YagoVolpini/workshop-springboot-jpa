package com.example.demo.services;

import com.example.demo.dto.OrderDTO;
import com.example.demo.entities.Order;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderDTO> findAll() {
        return orderRepository.findAll()
                .stream().map(OrderDTO::new).toList();
    }

    public OrderDTO findById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        return new OrderDTO(order);
    }
}
