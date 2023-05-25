package com.example.eshop.domain.discount;

import com.example.eshop.domain.basket.BasketItem;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.security.InvalidParameterException;

@Entity
public class BuyXGetYDiscount extends BaseDiscount{

    private int buy;
    private int free;

    public BuyXGetYDiscount() {
    }

    public BuyXGetYDiscount(int buy, int free) {
        if (buy < 1) {
            throw new InvalidParameterException("Can't buy less than 1 item");
        }

        if (free < 1) {
            throw new InvalidParameterException("Can't free less than 1 item for free");
        }

        if (free > buy) {
            throw new InvalidParameterException("The free amount must not exceed the purchased amount");
        }

        this.buy = buy;
        this.free = free;
    }


    @Override
    public BigDecimal apply(BasketItem basketItem) {

        int quantity = basketItem.getQuantity();
        BigDecimal unitPrice = basketItem.getProduct().getUnitPrice();

        if (buy + free > quantity && quantity > buy) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity - buy));
        }
        int multiplier = quantity / (buy + free);
        int forFree = multiplier * free;
        return unitPrice.multiply(BigDecimal.valueOf(forFree));
    }

    @Override
    public String description() {
        return String.format("Buy %s Get %s FREE", buy, free);
    }

    public int getBuy() {
        return buy;
    }

    public int getFree() {
        return free;
    }
}
