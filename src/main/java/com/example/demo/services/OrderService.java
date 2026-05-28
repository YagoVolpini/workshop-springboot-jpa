package com.example.demo.services;

import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.OrderInsertDTO;
import com.example.demo.dto.OrderItemInsertDTO;
import com.example.demo.dto.OrderStatusDTO;
import com.example.demo.entities.Order;
import com.example.demo.entities.OrderItem;
import com.example.demo.entities.Product;
import com.example.demo.entities.User;
import com.example.demo.enums.OrderStatus;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.exceptions.BusinessException;
import com.example.demo.services.exceptions.DatabaseException;
import com.example.demo.services.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<OrderDTO> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(OrderDTO::new);
    }

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        return new OrderDTO(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderDTO insert(OrderInsertDTO dto) {
        User client = userRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException(dto.getClientId()));

        Order order = new Order();
        order.setClient(client);
        order.setMoment(Instant.now());
        order.setStatus(OrderStatus.WAITING_PAYMENT);

        for (OrderItemInsertDTO itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(itemDto.getProductId()));

            if (product.getStock() < itemDto.getQuantity()) {
                throw new BusinessException(String.format("Stock insufficient for product '%s'. Available: %d, Requested: %d"
                        , product.getName(), product.getStock(), itemDto.getQuantity()));
            }
            product.setStock(product.getStock() - itemDto.getQuantity());
            productRepository.save(product);
            order.addItem(product, itemDto.getQuantity());
        }

        order = orderRepository.save(order);

        return new OrderDTO(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderDTO updateStatus(Long id, OrderStatusDTO dto) {


        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        if (order.getStatus() == dto.getStatus()) {

            return new OrderDTO(order);
        }

        if (order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.REFUNDED) {
            throw new BusinessException("Cannot update status of a finalized order");
        }

        if (order.getStatus() == OrderStatus.DELIVERED && dto.getStatus() != OrderStatus.REFUNDED) {
            throw new BusinessException("Cannot update status of a delivered order unless it is for REFUNDED");
        }

        if (dto.getStatus() == OrderStatus.CANCELED || dto.getStatus() == OrderStatus.REFUNDED) {
            for (OrderItem orderItem : order.getOrderItems()) {
                Product product = orderItem.getProduct();
                product.setStock(product.getStock() + orderItem.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(dto.getStatus());
        order = orderRepository.save(order);
        return new OrderDTO(order);
    }


    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        try {
            orderRepository.delete(order);
            orderRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Cannot delete this order because it is associated with other records");
        }
    }
}
