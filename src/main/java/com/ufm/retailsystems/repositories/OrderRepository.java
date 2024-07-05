package com.ufm.retailsystems.repositories;

import com.ufm.retailsystems.dto.forlist.DailyReportDTO;
import com.ufm.retailsystems.dto.forlist.DailyRevenueDTO;
import com.ufm.retailsystems.entities.DeliveryStatus;
import com.ufm.retailsystems.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Order findByOrderIdAndShipPhone(String orderId, String shipPhone);

    @Query("SELECT od.product.category.categoryName, SUM(od.quantity * od.unitPrice) " +
            "FROM OrderDetail od " +
            "JOIN od.product p " +
            "JOIN p.category c " +
            "JOIN od.order o " +
            "WHERE MONTH(o.orderDate) = :month AND YEAR(o.orderDate) = :year " +
            "GROUP BY c.categoryName")
    List<Object[]> calculateRevenueByCategory(@Param("month") int month, @Param("year") int year);


    @Query("SELECT new com.ufm.retailsystems.dto.forlist.DailyReportDTO(o.orderDate, SUM(o.totalPayment)) " +
            "FROM Order o " +
            "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY o.orderDate " +
            "ORDER BY o.orderDate")
    List<DailyReportDTO> findDailyReport(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT new com.ufm.retailsystems.dto.forlist.DailyRevenueDTO(o.orderDate, SUM(o.totalPayment)) " +
            "FROM Order o " +
            "WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month " +
            "GROUP BY o.orderDate")
    List<DailyRevenueDTO> findMonthlyRevenue(@Param("year") int year, @Param("month") int month);


}
