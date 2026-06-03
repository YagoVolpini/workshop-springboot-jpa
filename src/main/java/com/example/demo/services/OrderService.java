package com.example.demo.services;

import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.OrderInsertDTO;
import com.example.demo.dto.OrderItemInsertDTO;
import com.example.demo.dto.OrderStatusDTO;
import com.example.demo.entities.*;
import com.example.demo.enums.OrderStatus;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.PaymentRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.repositories.projections.ProductCustomersProjection;
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
    private final PaymentRepository paymentRepository;

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
            processOrderItem(order, itemDto);
        }
        return new OrderDTO(orderRepository.save(order));
    }


    private void processOrderItem(Order order, OrderItemInsertDTO itemDto) {
        Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(itemDto.getProductId()));
        if (product.getStock() < itemDto.getQuantity())
            throw new BusinessException(String.format("Stock insufficient for product '%s'. Available: %d, Requested: %d",
                    product.getName(), product.getStock(), itemDto.getQuantity()));

        product.setStock(product.getStock() - itemDto.getQuantity());
        productRepository.save(product);
        order.addItem(product, itemDto.getQuantity());
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderDTO updateStatus(Long id, OrderStatusDTO dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        validateStatusTransition(order, dto.getStatus());
        handleStockRestore(order, dto.getStatus());
        handlePayment(order, dto.getStatus());

        order.setStatus(dto.getStatus());
        return new OrderDTO(orderRepository.save(order));
    }

    private void validateStatusTransition(Order order, OrderStatus newStatus) {
        if (order.getStatus() == newStatus)
            throw new BusinessException(String.format("The order is already in the requested status: %s", newStatus));

        if (order.getStatus() == OrderStatus.CANCELED
                || order.getStatus() == OrderStatus.REFUNDED
                || (order.getStatus() == OrderStatus.DELIVERED && newStatus != OrderStatus.REFUNDED)
                || (order.getStatus() == OrderStatus.PAID && newStatus != OrderStatus.REFUNDED && newStatus != OrderStatus.SHIPPED)
                || (order.getStatus() == OrderStatus.SHIPPED && newStatus != OrderStatus.REFUNDED && newStatus != OrderStatus.DELIVERED))
            throw new BusinessException("Invalid status transition. This update is not allowed for the current order state.");

    }

    private void handleStockRestore(Order order, OrderStatus newStatus) {
        if (newStatus == OrderStatus.CANCELED || newStatus == OrderStatus.REFUNDED) {
            for (OrderItem orderItem : order.getOrderItems()) {
                Product product = orderItem.getProduct();
                product.setStock(product.getStock() + orderItem.getQuantity());
                productRepository.save(product);
            }
        }
    }

    private void handlePayment(Order order, OrderStatus newStatus) {
        if (newStatus == OrderStatus.PAID && order.getPayment() == null) {
            Payment payment = new Payment();
            payment.setMoment(Instant.now());
            payment.setOrder(order);
            order.setPayment(payment);

        } else if ((newStatus == OrderStatus.CANCELED || newStatus == OrderStatus.REFUNDED)
                && order.getPayment() != null) {

            paymentRepository.delete(order.getPayment());
            order.setPayment(null);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));

        if (order.getStatus() != OrderStatus.WAITING_PAYMENT) {
            throw new DatabaseException("Cannot delete this order because its current status is: " + order.getStatus());
        }
        try {
            orderRepository.delete(order);
            orderRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Cannot delete this order because it is associated with other records");
        }
    }

    @Transactional(readOnly = true)
    public Page<ProductCustomersProjection> findByIdOrNameProducts(Long id, String name, Pageable pageable) {

        if (id == null && (name == null || name.isBlank())) {
            throw new BusinessException("Provide at least an id or a product name");
        }

        if (id != null) {
            Page<ProductCustomersProjection> page = orderRepository.findByOrderItemsProductId(id, pageable);
            if (page.isEmpty()) throw new ResourceNotFoundException(id);
            return page;

        }

        Page<ProductCustomersProjection> page = orderRepository.findByOrderItemsProductNameContainingIgnoreCase(name, pageable);
        if (page.isEmpty())
            throw new BusinessException("No product found with name: " + name);
        return page;
    }
}