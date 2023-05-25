package com.example.eshop.service;

import com.example.eshop.data.repository.ProductRepository;
import com.example.eshop.domain.discount.BaseDiscount;
import com.example.eshop.domain.discount.Discount;
import com.example.eshop.domain.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class SimpleProductService implements ProductService{

    private final ProductRepository repository;

    @Autowired
    public SimpleProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Product> allProducts() {
        return repository.findAll();
    }

    @Override
    public Product productById(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found  in repository with " + id));
    }

    @Override
    public Product productByName(String name){
        return repository.productByName(name);
    }

    @Override
    public Product save(Product product) {
        return repository.save(product);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Product addDiscountToProduct(Product product, Discount discount) {
        product.addDiscount((BaseDiscount) discount);
        return repository.save(product);
    }
}
