package com.example.eshop.domain.basket;

import com.example.eshop.domain.discount.BaseDiscount;
import com.example.eshop.domain.product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Comparator;

@Entity
@Table(name = "basket_item")
public class BasketItem implements BasketItemOperations {
    @EmbeddedId
    @JsonIgnore
    private BasketProductKey basketProductKey;
    private Integer quantity;

    public BasketItem() {
    }

    public BasketItem(Basket basket, Product product, Integer quantity) {
        basketProductKey = new BasketProductKey(basket, product);
        this.quantity = quantity;
    }


    @Transient
    @Override
    public Integer getQuantity() {
        return quantity;
    }

    @Transient
    @Override
    public Product getProduct() {
        return basketProductKey.getProduct();
    }

    @Transient
    @Override
    public void increment() {
        quantity++;
    }

    @Transient
    @Override
    public void decrement() {
        if (quantity > 0) {
            quantity--;
        }
    }

    @Transient
    @Override
    public BigDecimal fullPrice() {
        return getProduct().getUnitPrice().multiply(new BigDecimal(quantity));
    }

    @Transient
    @Override
    public BigDecimal discount() {
        return getProduct().getDiscounts().stream()
                .map(discount -> discount.apply(this))
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }

    public BaseDiscount appliedDiscount() {
        return getProduct().getDiscounts().stream()
                .min((Comparator.comparing(a -> a.apply(this))))
                .orElse(null);
    }
}
