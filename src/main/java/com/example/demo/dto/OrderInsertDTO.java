package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderInsertDTO implements Serializable {

    @NotNull(message = "Client is required")
    private Long clientId;

    @NotEmpty(message = "Order must have at least one item")
    @Valid
    private List<OrderItemInsertDTO> items = new ArrayList<>();


    public OrderInsertDTO(Long clientId, List<OrderItemInsertDTO> items) {
        this.clientId = clientId;
        this.items = items != null ? items : new ArrayList<>();
    }

}
