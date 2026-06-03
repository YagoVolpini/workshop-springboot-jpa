package com.example.demo.repositories.projections;

import java.math.BigDecimal;

public interface ProductMinProjection {

    Long getId();

    String getName();

    BigDecimal getPrice();

    Integer getStock();
}
