package com.example.eshop.service;

import com.example.eshop.data.repository.DiscountRepository;
import com.example.eshop.domain.discount.BuyXGetYDiscount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimpleDiscountServiceTest {

    @Mock
    private DiscountRepository repository;

    @InjectMocks
    private SimpleDiscountService discountService;

    @Test
    void discountById() {
        final BuyXGetYDiscount discount = new BuyXGetYDiscount(1, 1);
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(discount));

        Assertions.assertEquals(discount, discountService.discountById(1L));
    }

    @Test
    void save() {
        final BuyXGetYDiscount discount = new BuyXGetYDiscount(1, 1);
        when(repository.save(discount)).thenReturn(discount);

        Assertions.assertEquals(discount, discountService.save(discount));
    }

    @Test
    void allDiscounts() {
        when(repository.findAll()).thenReturn(List.of(new BuyXGetYDiscount(1, 1), new BuyXGetYDiscount(2, 2)));

        assertEquals(2, discountService.allDiscounts().size());
    }
}