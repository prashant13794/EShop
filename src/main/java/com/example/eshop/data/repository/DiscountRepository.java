package com.example.eshop.data.repository;

import com.example.eshop.domain.discount.BaseDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

@Repository
public interface DiscountRepository extends JpaRepository<BaseDiscount, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    void deleteById(Long id);
}
