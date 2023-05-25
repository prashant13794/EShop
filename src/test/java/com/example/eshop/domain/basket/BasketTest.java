package com.example.eshop.domain.basket;

import com.example.eshop.domain.discount.BaseDiscount;
import com.example.eshop.domain.product.Product;
import com.example.eshop.exceptions.ProductOutOfStockException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class BasketTest {

    @Test
    void canAddProduct() throws ProductOutOfStockException {
        Basket basket = new Basket();
        Product product = new Product("name1", new BigDecimal(10), 1);
        basket.addProduct(product);
        assertEquals(1, basket.getBasketItems().size());
        assertEquals(product, basket.getBasketItems().get(0).getProduct());
        assertEquals(1, basket.getBasketItems().get(0).getQuantity());

    }

    @Test
    void willIncrementQuantityIfProductAlreadyExists() throws ProductOutOfStockException {
        Basket basket = new Basket();
        Product product = new Product("name1", new BigDecimal(10), 1);
        basket.addProduct(product);
        basket.addProduct(product);
        assertEquals(1, basket.getBasketItems().size());
        assertEquals(product, basket.getBasketItems().get(0).getProduct());
        assertEquals(2, basket.getBasketItems().get(0).getQuantity());
    }


    @Test
    void willThrowExceptionIfProductOutOfStock() {
        Basket basket = new Basket();
        Product product = new Product("name1", new BigDecimal(10), 0);
        assertThrows(ProductOutOfStockException.class, () -> {
            basket.addProduct(product);
        });


    }

    @Test
    void canRemoveProduct() throws ProductOutOfStockException {
        Basket basket = new Basket();
        Product product = new Product("name1", new BigDecimal(10), 1);
        basket.addProduct(product);
        basket.removeProduct(product);
        assertEquals(0, basket.getBasketItems().size());
    }

    @Test
    void willDecrementQuantityIfMoreThanOne() throws ProductOutOfStockException {
        Basket basket = new Basket();
        Product product = new Product("name1", new BigDecimal(10), 1);
        basket.addProduct(product);
        basket.addProduct(product);

        assertEquals(2, basket.getBasketItems().get(0).getQuantity());

        basket.removeProduct(product);

        assertEquals(1, basket.getBasketItems().size());
        assertEquals(product, basket.getBasketItems().get(0).getProduct());
        assertEquals(1, basket.getBasketItems().get(0).getQuantity());
    }

    @Test
    void canCalculateTotalAmountAndReceipt() throws ProductOutOfStockException {
        Basket basket = new Basket();
        Product product1 = new Product("name1", new BigDecimal(10), 1);
        Product product2 = new Product("name2", new BigDecimal(10), 1);
        final BaseDiscount discount = Mockito.mock(BaseDiscount.class);
        Mockito.when(discount.apply(any(BasketItem.class))).thenReturn(BigDecimal.valueOf(10));
        Mockito.when(discount.description()).thenReturn("BUY 1 GET 1 FREE");
        product1.addDiscount(discount);
        product2.addDiscount(discount);
        basket.addProduct(product1);
        basket.addProduct(product1);
        basket.addProduct(product2);
        assertEquals(new BigDecimal(10), basket.totalAmount());
        String expectedReceipt = String.format("%s\n%s\n%s\n%s\n%s\n%s\n"
                , "                    Product Name  Quantity           Price"
                , "                           name1         2              20"
                , "      BUY 1 GET 1 FREE     name1                       -10"
                , "                           name2         1              10"
                , "      BUY 1 GET 1 FREE     name2                       -10"
                , "                   Total price :                        10"
        );


        assertEquals(expectedReceipt, basket.receipt());

    }

    @Test
    void receipt() {
    }
}

