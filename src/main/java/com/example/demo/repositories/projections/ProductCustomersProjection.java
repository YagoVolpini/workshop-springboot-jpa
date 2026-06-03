package com.example.demo.repositories.projections;

import java.time.Instant;

public interface ProductCustomersProjection {

    Long getId();

    Instant getMoment();

    String getStatus();

    Long getClientId();

    String getClientName();

    String getClientEmail();

    String getClientPhone();

    String getOrderItemsProductName();
}
