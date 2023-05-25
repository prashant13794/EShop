package com.example.eshop.data.repository;

import com.example.eshop.domain.basket.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    void deleteById(Long id);
}
