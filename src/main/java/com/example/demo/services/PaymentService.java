package com.example.demo.services;

import com.example.demo.dto.PaymentDTO;
import com.example.demo.entities.Payment;
import com.example.demo.repositories.PaymentRepository;
import com.example.demo.services.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Page<PaymentDTO> findAll(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(PaymentDTO::new);
    }

    public PaymentDTO findById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        return new PaymentDTO(payment);
    }
}
