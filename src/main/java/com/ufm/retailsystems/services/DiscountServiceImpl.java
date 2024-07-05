package com.ufm.retailsystems.services;

import com.ufm.retailsystems.dto.forcreate.CDiscount;
import com.ufm.retailsystems.entities.Discount;
import com.ufm.retailsystems.repositories.DiscountRepository;
import com.ufm.retailsystems.services.templates.IDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiscountServiceImpl implements IDiscountService {
    @Autowired
    private DiscountRepository discountRepository;

    public List<Discount> findAll() {
        return discountRepository.findAll();
    }

    @Override
    public Discount add(CDiscount cDiscount){
        Discount discount = new Discount();
        discount.setDiscountDescription(cDiscount.getDescription());
        discount.setDiscountPercent(cDiscount.getPercent());
        discount.setStartDate(cDiscount.getStartDay());
        discount.setEndDate(cDiscount.getEndDay());
        return discountRepository.save(discount);
    }

    @Override
    public void deleteById(Long id) {
        discountRepository.deleteById(id);
    }

    public Discount findById(Long id ) {
        return discountRepository.findByDiscountId(id);
    }

    public Discount update(Discount cDiscount) {
        return discountRepository.save(cDiscount);
    }

    public void delete(Long discountId) {
        discountRepository.deleteDiscountByDiscountId(discountId);
    }
}
