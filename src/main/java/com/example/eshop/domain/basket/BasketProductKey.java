package com.example.eshop.domain.basket;

import com.example.eshop.domain.product.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BasketProductKey implements Serializable {

    @JsonBackReference
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "basket_id")
    private Basket basket;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    public BasketProductKey() {
    }

    public BasketProductKey(Basket basket, Product product) {
        this.basket = basket;
        this.product = product;
    }

    public Basket getBasket() {
        return basket;
    }

    public Product getProduct() {
        return product;
    }
}
