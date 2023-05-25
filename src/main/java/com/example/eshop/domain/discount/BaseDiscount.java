package com.example.eshop.domain.discount;

import javax.persistence.*;

@Entity
@Table(name = "Discount")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BaseDiscount implements Discount{

    @Id
    @GeneratedValue
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    public BaseDiscount() {
    }
}
