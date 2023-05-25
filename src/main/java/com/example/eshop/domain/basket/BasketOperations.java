package com.example.eshop.domain.basket;

import com.example.eshop.domain.product.Product;
import com.example.eshop.exceptions.ProductOutOfStockException;

import javax.persistence.Transient;
import java.math.BigDecimal;

public interface BasketOperations {

    void addProduct(Product product) throws ProductOutOfStockException;

    @Transient
    void removeProduct(Product product);

    BigDecimal totalAmount();
    String receipt();
}
