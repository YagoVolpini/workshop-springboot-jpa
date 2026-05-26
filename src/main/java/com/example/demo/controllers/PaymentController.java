package com.example.demo.controllers;

import com.example.demo.dto.PaymentDTO;
import com.example.demo.services.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @GetMapping
    public ResponseEntity<Page<PaymentDTO>> findAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(paymentService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }
}
