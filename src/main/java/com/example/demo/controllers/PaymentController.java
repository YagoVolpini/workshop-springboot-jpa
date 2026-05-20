package com.example.demo.controllers;

import com.example.demo.entities.Payment;
import com.example.demo.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @GetMapping
    public ResponseEntity<List<Payment>> findAll() {
        return ResponseEntity.ok(paymentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> findById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }
}
