package com.example.eshop.service;

import com.example.eshop.domain.product.Product;
import com.example.eshop.domain.basket.Basket;
import com.example.eshop.exceptions.ProductOutOfStockException;

import java.util.List;

public interface BasketService {

    List<Basket> allBaskets();
    Basket basketById(Long id);
    Basket save(Basket basket);
    void delete(Long id);
    Basket addProductToBasket(Long basketId, Long productId) throws ProductOutOfStockException;
    Basket removeProductFromBasket(Basket basket, Product product);

}
