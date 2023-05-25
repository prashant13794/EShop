package com.example.eshop.service;

import com.example.eshop.data.repository.ProductRepository;
import com.example.eshop.domain.product.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimpleProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private SimpleProductService productService;

    @Test
    void allProducts() {
        when(repository.findAll()).thenReturn(List.of(new Product(), new Product()));

        assertEquals(2, productService.allProducts().size());
    }

    @Test
    void productById() {
        final Product product = new Product();
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(product));

        assertEquals(product, productService.productById(1L));
    }

    @Test
    void productByName() {
        final Product product = new Product();
        when(repository.productByName("foo")).thenReturn(product);

        assertEquals(product, productService.productByName("foo"));
    }

    @Test
    void save() {
        final Product product = new Product();
        when(repository.save(product)).thenReturn(product);

        assertEquals(product, productService.save(product));
    }
}