package com.example.eshop.service;

import com.example.eshop.domain.discount.Discount;
import com.example.eshop.domain.product.Product;

import java.util.List;

public interface ProductService {

    List<Product> allProducts();
    Product productById(Long id);
    Product productByName(String name);
    Product save(Product product);
    void delete(Long id);
    Product addDiscountToProduct(Product product, Discount discount);

}
