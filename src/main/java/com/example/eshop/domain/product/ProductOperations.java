package com.example.eshop.domain.product;

import com.example.eshop.domain.discount.BaseDiscount;

public interface ProductOperations {

    void addDiscount(BaseDiscount discount);
    boolean inStock();
    void incrementStock();
    void decrementStock();
}
