package com.example.eshop.service;

import com.example.eshop.data.repository.BasketRepository;
import com.example.eshop.domain.product.Product;
import com.example.eshop.domain.basket.Basket;
import com.example.eshop.exceptions.ProductOutOfStockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class SimpleBasketService implements BasketService{

    private final BasketRepository basketRepository;
    private final ProductService productService;

    @Autowired
    public SimpleBasketService(BasketRepository basketRepository, ProductService productService) {
        this.basketRepository = basketRepository;
        this.productService = productService;
    }

    @Override
    public List<Basket> allBaskets() {
        return basketRepository.findAll();
    }

    @Override
    public Basket basketById(Long id) {
        return basketRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Basket not found  in repository with " + id));
    }

    @Override
    public Basket save(Basket basket) {
        return basketRepository.save(basket);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        basketRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Basket addProductToBasket(Long basketId, Long productId) throws ProductOutOfStockException {
        Product product = productService.productById(productId);
        Basket basket = basketById(basketId);
        basket.addProduct(product);
        product.decrementStock();
        productService.save(product);
        return basketRepository.save(basket);
    }

    @Override
    @Transactional
    public Basket removeProductFromBasket(Basket basket, Product product) {
        basket.removeProduct(product);
        product.incrementStock();
        productService.save(product);
        return basketRepository.save(basket);
    }
}
