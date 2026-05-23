package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderInsertDTO implements Serializable {

    @NotNull(message = "Client is required")
    private Long clientId;

    @NotEmpty(message = "Order must have at least one item")
    @Valid
    private List<OrderItemInsertDTO> items = new ArrayList<>();

    public OrderInsertDTO() {
    }

    public OrderInsertDTO(Long clientId, List<OrderItemInsertDTO> items) {
        this.clientId = clientId;
        this.items = items != null ? items : new ArrayList<>();
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public List<OrderItemInsertDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemInsertDTO> items) {
        this.items = items;
    }
}
