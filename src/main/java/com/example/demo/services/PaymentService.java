package com.example.demo.services;

import com.example.demo.dto.PaymentDTO;
import com.example.demo.entities.Payment;
import com.example.demo.repositories.PaymentRepository;
import com.example.demo.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<PaymentDTO> findAll() {
        return paymentRepository.findAll().stream().map(PaymentDTO::new).toList();
    }

    public PaymentDTO findById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        return new PaymentDTO(payment);
    }
}
