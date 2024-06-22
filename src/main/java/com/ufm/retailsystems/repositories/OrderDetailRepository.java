package com.ufm.retailsystems.repositories;

import com.ufm.retailsystems.entities.Order;
import com.ufm.retailsystems.entities.OrderDetail;
import com.ufm.retailsystems.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.Valid;
import java.util.List;


@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail,Long> {

    @Query("SELECT od from OrderDetail od where od.order.orderId = :orderId ")
    List<OrderDetail> findAllByOrderId(@Param("orderId") String orderId);
}
