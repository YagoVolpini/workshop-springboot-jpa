package com.example.demo.controllers;

import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.OrderInsertDTO;
import com.example.demo.dto.OrderStatusDTO;
import com.example.demo.repositories.projections.ProductCustomersProjection;
import com.example.demo.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderDTO>> findAll(
            @PageableDefault(page = 0, size = 10, sort = "moment") Pageable pageable) {
        return ResponseEntity.ok(orderService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderDTO> insert(@Valid @RequestBody OrderInsertDTO order) {
        OrderDTO obj = orderService.insert(order);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getId())
                .toUri();
        return ResponseEntity.created(uri).body(obj);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusDTO status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products-sales")
    public ResponseEntity<Page<ProductCustomersProjection>> getProductCustomers(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(orderService.findByIdOrNameProducts(id, name, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> update(@PathVariable Long id,@RequestBody OrderInsertDTO order) {
        return ResponseEntity.ok(orderService.update(id, order));
    }
}
