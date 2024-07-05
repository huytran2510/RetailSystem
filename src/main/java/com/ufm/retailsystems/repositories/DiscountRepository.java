package com.ufm.retailsystems.repositories;

import com.ufm.retailsystems.entities.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DiscountRepository extends JpaRepository<Discount,Long> {

    Discount findByDiscountId(Long id);

    void deleteDiscountByDiscountId(Long discountId);
}
