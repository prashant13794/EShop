package com.example.eshop.domain.discount;

import com.example.eshop.domain.basket.BasketItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(BuyXGetYDiscount.class)}
)
public interface Discount {

    BigDecimal apply(BasketItem basketItem);

    Long getId();
    String description();

}
