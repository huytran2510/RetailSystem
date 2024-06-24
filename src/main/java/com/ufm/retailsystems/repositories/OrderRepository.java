package com.ufm.retailsystems.repositories;

import com.ufm.retailsystems.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    public Order findByOrderId(String orderId);

    @Query("SELECT o FROM Order o WHERE o.orderDate = :date")
    List<Order> findAllByOrderDate(LocalDate date);

    @Query("SELECT o FROM Order o WHERE YEAR(o.orderDate) = :year")
    List<Order> findAllByOrderYear(int year);

    @Query("SELECT o FROM Order o WHERE MONTH(o.orderDate) = :month AND YEAR(o.orderDate) = :year")
    List<Order> findAllByOrderMonth(int month, int year);

    @Query("SELECT COUNT(o) FROM Order o WHERE MONTH(o.orderDate) = :month AND YEAR(o.orderDate) = :year GROUP BY DAY(o.orderDate)")
    List<Long> countOrdersByDayInMonth(int month, int year);


    Page<Order> findByOrderDate(LocalDate orderDate, Pageable pageable);

    Page<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Order> findByOrderDateYear(int year, Pageable pageable);
}
