package com.example.demo.dto;

import com.example.demo.enums.OrderStatus;

import java.io.Serializable;

public class OrderStatusDTO implements Serializable {

    private OrderStatus status;

    public OrderStatusDTO() {
    }

    public OrderStatusDTO(OrderStatus status) {
        this.status = status;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
