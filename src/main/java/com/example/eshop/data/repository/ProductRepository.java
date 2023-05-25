package com.example.eshop.data.repository;

import com.example.eshop.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.name = ?1")
    Product productByName(String name);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Product> findById(Long productId);
}
