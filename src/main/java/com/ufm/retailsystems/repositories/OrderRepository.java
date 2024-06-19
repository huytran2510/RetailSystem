package com.ufm.retailsystems.repositories;

import com.ufm.retailsystems.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    public Order findByOrderId(String orderId);
}
