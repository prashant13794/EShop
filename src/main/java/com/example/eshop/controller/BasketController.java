package com.example.eshop.controller;

import com.example.eshop.domain.basket.Basket;
import com.example.eshop.domain.product.Product;
import com.example.eshop.exceptions.ProductOutOfStockException;
import com.example.eshop.service.BasketService;
import com.example.eshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/baskets/")
public class BasketController {

    private final ProductService productService;
    private final BasketService basketService;

    @Autowired
    public BasketController(ProductService productService, BasketService basketService) {
        this.productService = productService;
        this.basketService = basketService;
    }

    @GetMapping
    public Iterable<Basket> getBaskets() {
        return basketService.allBaskets();
    }

    @PostMapping
    public Basket addBasket(@RequestBody Basket basket) {
        return basketService.save(basket);
    }

    @GetMapping("{id}")
    public Basket getBasket(@PathVariable Long id) {
        return basketService.basketById(id);
    }

    @PostMapping("{basket_id}/products/{product_id}")
    public Basket addProduct(@PathVariable("basket_id") Long basketId, @PathVariable("product_id") Long productId) throws ProductOutOfStockException {
        return basketService.addProductToBasket(basketId, productId);
    }

    @DeleteMapping("{basket_id}/products/{product_id}")
    public Basket removeProduct(@PathVariable("basket_id") Long basketId, @PathVariable("product_id") Long productId) {

        Product product = productService.productById(productId);
        Basket basket = basketService.basketById(basketId);
        return basketService.removeProductFromBasket(basket, product);
    }

    @GetMapping("{basket_id}/receipt")
    public String getReceipt(@PathVariable("basket_id") Long basketId) {

        Basket basket = basketService.basketById(basketId);
        return basket.receipt();
    }

    @GetMapping("{basket_id}/total")
    public BigDecimal getTotal(@PathVariable("basket_id") Long basketId) {

        Basket basket = basketService.basketById(basketId);
        return basket.totalAmount();
    }
}