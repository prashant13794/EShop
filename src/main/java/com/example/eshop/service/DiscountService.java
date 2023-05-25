package com.example.eshop.service;

import com.example.eshop.domain.discount.Discount;

import java.util.List;

public interface DiscountService {

    Discount discountById(Long id);
    Discount save(Discount discount);
    void delete(Long id);
    List<? extends Discount> allDiscounts();
}
