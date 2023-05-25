package com.example.eshop.domain.basket;

import com.example.eshop.domain.discount.BaseDiscount;
import com.example.eshop.domain.product.Product;
import com.example.eshop.exceptions.ProductOutOfStockException;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "basket")
public class Basket implements BasketOperations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "basketProductKey.basket", orphanRemoval = true)
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
    private List<BasketItem> basketItems = new ArrayList<>();

    public Basket() {
    }

    @Transient
    @Override
    @Transactional
    public void addProduct(Product product) throws ProductOutOfStockException {
        if (!product.inStock()) {
            throw new ProductOutOfStockException("Product is Out Of Stock!");
        }
        Optional<BasketItem> basketItemOptional = basketItems.stream().filter(basketItem -> basketItem.getProduct().equals(product)).findFirst();

        if (basketItemOptional.isPresent()) {
            basketItemOptional.get().increment();
            return;
        }
        basketItems.add(new BasketItem(this, product, 1));
    }

    @Transient
    @Override
    public void removeProduct(Product product) {
        Optional<BasketItem> basketItemOptional = basketItems.stream().filter(basketItem -> basketItem.getProduct().equals(product)).findFirst();

        if (basketItemOptional.isPresent()) {
            if (basketItemOptional.get().getQuantity() > 1) {
                basketItemOptional.get().decrement();
            } else {
                basketItemOptional.ifPresent(basketItem -> basketItems.remove(basketItem));
            }
            return;
        }

        throw new IllegalArgumentException("Product not found in basket :" + product.getId());
    }

    @Transient
    @Override
    public BigDecimal totalAmount() {
        return basketItems.stream()
                .map(BasketItem::fullPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .subtract(basketItems.stream()
                        .map(BasketItem::discount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Transient
    @Override
    public String receipt() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%32s%10s%16s\n", "Product Name", "Quantity", "Price"));
        basketItems.forEach((basketItem) -> {
            final Product product = basketItem.getProduct();
            sb.append(String.format("%32s%10s%16s\n", product.getName(), basketItem.getQuantity(), basketItem.fullPrice()));

            final BaseDiscount appliedDiscount = basketItem.appliedDiscount();
            if (appliedDiscount != null) {
                sb.append(String.format("%22s%10s%26s\n", appliedDiscount.description(), product.getName(), basketItem.discount().negate()));
            }
        });

        return sb.append(String.format("%32s%26s\n", "Total price :", totalAmount())).toString();
    }

    public Long getId() {
        return id;
    }

    public List<BasketItem> getBasketItems() {
        return basketItems;
    }
}
