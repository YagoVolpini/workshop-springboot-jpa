package com.example.demo.repositories;

import com.example.demo.entities.Order;
import com.example.demo.entities.User;
import com.example.demo.repositories.projections.ProductCustomersProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<ProductCustomersProjection> findByOrderItemsProductId(Long id, Pageable pageable);

    Page<ProductCustomersProjection> findByOrderItemsProductNameContainingIgnoreCase(String productName, Pageable pageable);

    Page<ProductCustomersProjection> findByOrderItemsProductIdAndClient(Long id, User client, Pageable pageable);
    Page<ProductCustomersProjection> findByOrderItemsProductNameContainingIgnoreCaseAndClient(String productName, User client, Pageable pageable);

    Page<Order> findByClient(User client, Pageable pageable);
}
