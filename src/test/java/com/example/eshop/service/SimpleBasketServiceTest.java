package com.example.eshop.service;

import com.example.eshop.data.repository.BasketRepository;
import com.example.eshop.domain.basket.Basket;
import com.example.eshop.domain.product.Product;
import com.example.eshop.exceptions.ProductOutOfStockException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimpleBasketServiceTest {

    @Mock
    private BasketRepository repository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private SimpleBasketService simpleBasketService;

    @Test
    void allBaskets() {

        when(repository.findAll()).thenReturn(List.of(new Basket(), new Basket()));

        assertEquals(2, simpleBasketService.allBaskets().size());
    }

    @Test
    void basketById() {
        final Basket basket = new Basket();
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(basket));

        Assertions.assertEquals(basket, simpleBasketService.basketById(1L));
    }

    @Test
    void save() {
        final Basket basket = new Basket();
        when(repository.save(basket)).thenReturn(basket);

        Assertions.assertEquals(basket, simpleBasketService.save(basket));
    }

    @Test
    void addProductToBasket() throws ProductOutOfStockException {

        final Basket basket = new Basket();
        final Product product = new Product("name1", new BigDecimal(10), 1);
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(basket));
        when(repository.save(basket)).thenReturn(basket);
        when(productService.productById(1L)).thenReturn(product);
        when(productService.save(product)).thenReturn(product);

        Assertions.assertEquals(basket, simpleBasketService.addProductToBasket(1L, 1L));
        assertEquals(1, basket.getBasketItems().size());
        assertEquals(0, product.getStock());
    }

    @Test
    void addProductToBasketThrowsExceptionIfProductOutOfStock() throws ProductOutOfStockException {

        final Basket basket = new Basket();
        final Product product = new Product("name1", new BigDecimal(10), 0);
        when(productService.productById(1L)).thenReturn(product);
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(basket));


        assertThrows(ProductOutOfStockException.class, () -> simpleBasketService.addProductToBasket(1L, 1L));
    }

    @Test
    void removeProductFromBasket() throws ProductOutOfStockException {

        final Product product = new Product("name1", new BigDecimal(10), 1);
        final Basket basket = new Basket();
        basket.addProduct(product);
        when(repository.save(basket)).thenReturn(basket);
        when(productService.save(product)).thenReturn(product);

        Assertions.assertEquals(basket, simpleBasketService.removeProductFromBasket(basket, product));
        assertEquals(0, basket.getBasketItems().size());
        assertEquals(2, product.getStock());
    }

    @Test
    void removeProductNotInBasketThrowsException() {

        final Basket basket = new Basket();
        final Product product = new Product("name1", new BigDecimal(10), 1);
        assertThrows(IllegalArgumentException.class, () -> simpleBasketService.removeProductFromBasket(basket, product));
    }
}