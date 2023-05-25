package com.example.eshop.controller;

import com.example.eshop.domain.discount.Discount;
import com.example.eshop.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discounts/")
public class DiscountController {

    private final DiscountService discountService;

    @Autowired
    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @GetMapping
    public Iterable<Discount> getDiscounts() {
        return (Iterable<Discount>) discountService.allDiscounts();
    }

    @GetMapping("{id}")
    public Discount getDiscount(@PathVariable Long id) {
        return discountService.discountById(id);
    }

    @PostMapping
    public Discount addDiscount(@RequestBody Discount discount) {
        return discountService.save(discount);
    }
}
