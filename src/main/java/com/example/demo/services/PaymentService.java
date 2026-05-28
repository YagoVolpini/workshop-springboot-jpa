package com.example.demo.services;

import com.example.demo.dto.PaymentDTO;
import com.example.demo.entities.Payment;
import com.example.demo.repositories.PaymentRepository;
import com.example.demo.services.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public Page<PaymentDTO> findAll(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(PaymentDTO::new);
    }

    @Transactional(readOnly = true)
    public PaymentDTO findById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        return new PaymentDTO(payment);
    }
}
