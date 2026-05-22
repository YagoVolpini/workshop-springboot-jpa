package com.example.demo.dto;

import com.example.demo.entities.Order;
import com.example.demo.entities.OrderItem;
import com.example.demo.enums.OrderStatus;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO implements Serializable {

    private Long id;
    private Instant moment;
    private OrderStatus status;
    private UserDTO client;
    private List<OrderItemDTO> items = new ArrayList<>();
    private Double total;


    public OrderDTO() {
    }

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.moment = order.getMoment();
        this.status = order.getStatus();
        this.client = new UserDTO(order.getClient());
        this.total = order.getTotal();
        for (OrderItem item : order.getOrderItems()) {
            this.items.add(new OrderItemDTO(item));
        }

    }

    public UserDTO getClient() {
        return client;
    }

    public void setClient(UserDTO client) {
        this.client = client;
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

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
