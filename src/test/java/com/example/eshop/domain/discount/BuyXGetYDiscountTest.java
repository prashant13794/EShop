package com.example.eshop.domain.discount;

import com.example.eshop.domain.basket.BasketItem;
import com.example.eshop.domain.product.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BuyXGetYDiscountTest {


    @ParameterizedTest
    @CsvSource({"10,10,30", "15,2,0"})
    void apply(BigDecimal unitPrice, Integer quantity, BigDecimal expectedDiscount) {
        BuyXGetYDiscount discount = new BuyXGetYDiscount(2, 1);
        final BasketItem basketItem = new BasketItem(null, new Product("name1", unitPrice, 1), quantity);
        assertEquals(expectedDiscount, discount.apply(basketItem));
    }

    @ParameterizedTest
    @CsvSource({"1,0,Can't free less than 1 item for free", "0,1,Can't buy less than 1 item", "2,3,The free amount must not exceed the purchased amount"})
    void apply(int buy, int free, String expectedExceptionMessage) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new BuyXGetYDiscount(buy, free);
        });
        assertEquals(expectedExceptionMessage, exception.getMessage());

    }

    @Test
    void description() {
        BuyXGetYDiscount discount = new BuyXGetYDiscount(1, 1);
        assertEquals("Buy 1 Get 1 FREE", discount.description());
    }
}