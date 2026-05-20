package com.example.demo.services;

import com.example.demo.entities.Payment;
import com.example.demo.repositories.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Payment findById(Long id) {
        Optional<Payment> obj = paymentRepository.findById(id);
        return obj.get();
    }
}
