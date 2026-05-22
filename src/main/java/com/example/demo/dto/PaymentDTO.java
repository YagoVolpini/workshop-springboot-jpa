package com.example.demo.dto;

import com.example.demo.entities.Payment;

import java.io.Serializable;
import java.time.Instant;

public class PaymentDTO implements Serializable {

    private Long id;
    private Instant moment;

    public PaymentDTO() {
    }

    public PaymentDTO(Payment payment) {
        this.id = payment.getId();
        this.moment = payment.getMoment();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }
}
