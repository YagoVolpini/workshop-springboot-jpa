package com.example.demo.dto;

import com.example.demo.entities.Order;
import com.example.demo.entities.OrderItem;
import com.example.demo.enums.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderDTO implements Serializable {

    private Long id;
    private Instant moment;
    private OrderStatus status;
    private UserDTO client;
    private List<OrderItemDTO> items = new ArrayList<>();


    public OrderDTO(Order order) {
        this.id = order.getId();
        this.moment = order.getMoment();
        this.status = order.getStatus();
        this.client = new UserDTO(order.getClient());
        for (OrderItem item : order.getOrderItems()) {
            this.items.add(new OrderItemDTO(item));
        }

    }

    public BigDecimal getTotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (OrderItemDTO item : items) {
            sum = sum.add(item.getSubTotal());
        }
        return sum;
    }
}
