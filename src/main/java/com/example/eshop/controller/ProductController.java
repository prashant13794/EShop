package com.example.eshop.controller;

import com.example.eshop.domain.discount.Discount;
import com.example.eshop.domain.product.Product;
import com.example.eshop.service.DiscountService;
import com.example.eshop.service.ProductService;
import com.example.eshop.service.SimpleProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products/")
public class ProductController {

    private final ProductService productService;
    private final DiscountService discountService;

    @Autowired
    public ProductController(SimpleProductService productService, DiscountService discountService) {
        this.productService = productService;
        this.discountService = discountService;
    }

    @GetMapping
    public Iterable<Product> getProducts() {
        return productService.allProducts();
    }

    @GetMapping("{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.productById(id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void removeProduct(@PathVariable Long id) {
        productService.delete(id);
    }

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productService.save(product);
    }

    @PostMapping("{product_id}/discounts/{discount_id}")
    public Product  addDiscounts(@PathVariable("product_id") Long productId, @PathVariable("discount_id") Long discountId) {
        Product product = productService.productById(productId);
        Discount discount = discountService.discountById(discountId);
        return productService.addDiscountToProduct(product, discount);
    }

    @DeleteMapping("{product_id}/discounts/{discount_id}")
    public Product  removeDiscounts(@PathVariable("product_id") Long productId, @PathVariable("discount_id") Long discountId) {
        Product product = productService.productById(productId);
        Discount discount = discountService.discountById(discountId);
        return productService.addDiscountToProduct(product, discount);
    }
}
