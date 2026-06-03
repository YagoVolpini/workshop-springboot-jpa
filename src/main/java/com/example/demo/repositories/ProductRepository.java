package com.example.demo.repositories;

import com.example.demo.entities.Product;
import com.example.demo.repositories.projections.ProductMinProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface    ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);

    Page<Product> findAllByCategoriesNameContainingIgnoreCase(String name , Pageable pageable);

    Page<Product> findAllByCategoriesId(Long id, Pageable pageable);


    Page<ProductMinProjection> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<ProductMinProjection> findByIdIs(Long id, Pageable pageable);

}
