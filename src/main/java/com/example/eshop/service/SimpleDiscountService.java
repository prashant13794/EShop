package com.example.eshop.service;

import com.example.eshop.data.repository.DiscountRepository;
import com.example.eshop.domain.discount.BaseDiscount;
import com.example.eshop.domain.discount.Discount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class SimpleDiscountService implements DiscountService{

    private final DiscountRepository repository;

    @Autowired
    public SimpleDiscountService(DiscountRepository repository) {
        this.repository = repository;
    }

    @Override
    public Discount discountById(Long id) {
        return  repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Discount not found  in repository with " + id));
    }

    @Override
    public Discount save(Discount discount) {
        return repository.save((BaseDiscount)discount);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<? extends Discount> allDiscounts() {
        return repository.findAll();
    }
}
