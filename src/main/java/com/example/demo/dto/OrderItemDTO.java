package com.example.demo.dto;

import com.example.demo.entities.OrderItem;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderItemDTO implements Serializable {

    private Long id;
    private Integer quantity;
    private BigDecimal price;
    private ProductDTO product;

    public OrderItemDTO() {
    }

    public OrderItemDTO(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.quantity = orderItem.getQuantity();
        this.price = orderItem.getPrice();
        this.product = new ProductDTO(orderItem.getProduct());
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
