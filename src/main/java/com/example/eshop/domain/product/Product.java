package com.example.eshop.domain.product;

import com.example.eshop.domain.discount.BaseDiscount;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@JsonIgnoreProperties(ignoreUnknown = false)
public class Product implements ProductOperations{

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private BigDecimal unitPrice;
    @ManyToMany
    @JoinColumn(name = "discounts", referencedColumnName = "id")
    private List<BaseDiscount> discounts = new ArrayList<>();
    private Integer stock;

    public Product() {
    }

    public Product(String name, BigDecimal unitPrice, Integer stock) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.stock = stock;
    }

    @Transient
    @Override
    public void addDiscount(BaseDiscount discount) {
        this.discounts.add(discount);
    }

    @Override
    public boolean inStock() {
        return stock > 0;
    }

    @Transient
    @Override
    public void incrementStock() {
        stock++;
    }

    @Transient
    @Override
    public void decrementStock() {
        if (inStock()) {
            stock--;
        }
    }


    public Long getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }


    public Integer getStock() {
        return stock;
    }

    public List<BaseDiscount> getDiscounts() {
        return discounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) && Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
