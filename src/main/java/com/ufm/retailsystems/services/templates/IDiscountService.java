package com.ufm.retailsystems.services.templates;

import com.ufm.retailsystems.dto.forcreate.CDiscount;
import com.ufm.retailsystems.entities.Discount;

import java.util.List;

public interface IDiscountService {
    List<Discount> findAll();
    Discount add(CDiscount cDiscount);

    void deleteById(Long id);
    Discount findById(Long id );
    Discount update(Discount cDiscount);
    void delete(Long discountId);
}
