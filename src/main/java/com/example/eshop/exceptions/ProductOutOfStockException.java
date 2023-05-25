package com.example.eshop.exceptions;

public class ProductOutOfStockException extends Exception{
    public ProductOutOfStockException(String message) {
        super(message);
    }
}
