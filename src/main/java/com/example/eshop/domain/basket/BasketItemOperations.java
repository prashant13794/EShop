package com.example.eshop.domain.basket;

import com.example.eshop.domain.product.Product;

import javax.persistence.Transient;
import java.math.BigDecimal;

public interface BasketItemOperations {
    Integer getQuantity();
    Product getProduct();
    void increment();
    void decrement();

    @Transient
    BigDecimal fullPrice();

    @Transient
    BigDecimal discount();
}
