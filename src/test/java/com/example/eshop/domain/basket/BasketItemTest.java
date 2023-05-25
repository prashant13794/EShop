package com.example.eshop.domain.basket;

import com.example.eshop.domain.discount.BuyXGetYDiscount;
import com.example.eshop.domain.product.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BasketItemTest {

    @Test
    void canCalculatePriceAndDiscount() {
        Product product = new Product("name1", new BigDecimal(10), 1);
        final BuyXGetYDiscount discount = new BuyXGetYDiscount(2, 1);
        product.addDiscount(discount);
        BasketItem basketItem = new BasketItem(null, product, 3);
        assertEquals( new BigDecimal(30), basketItem.fullPrice());
        assertEquals( new BigDecimal(10), basketItem.discount());
        assertEquals( discount, basketItem.appliedDiscount());
    }

    @Test
    void canCalculatePriceAndDiscountWithMinValue() {
        Product product = new Product("name1", new BigDecimal(10), 1);
        final BuyXGetYDiscount discount1 = new BuyXGetYDiscount(1, 1);
        final BuyXGetYDiscount discount2 = new BuyXGetYDiscount(2, 1);
        product.addDiscount(discount1);
        product.addDiscount(discount2);
        BasketItem basketItem = new BasketItem(null, product, 4);
        assertEquals( new BigDecimal(40), basketItem.fullPrice());
        assertEquals( new BigDecimal(10), basketItem.discount());
        assertEquals( discount2, basketItem.appliedDiscount());
    }

    @Test
    void zeroDiscountIfNoDiscountsAddedOnProduct() {
        Product product = new Product("name1", new BigDecimal(10), 1);
        BasketItem basketItem = new BasketItem(null, product, 4);
        assertEquals( new BigDecimal(40), basketItem.fullPrice());
        assertEquals( new BigDecimal(0), basketItem.discount());
        assertNull(basketItem.appliedDiscount());
    }
}