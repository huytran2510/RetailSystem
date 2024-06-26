package com.ufm.retailsystems.services.templates;

import com.ufm.retailsystems.dto.cart.CartItem;
import com.ufm.retailsystems.dto.forcreate.COrder;
import com.ufm.retailsystems.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IOrderService {
    Order saveOrder(COrder cOrder, List<CartItem> itemList);
    Order findByOrderId(String orderId);

    List<Order> findAll();

    List<Order> getOrdersByDate(LocalDate date);
    List<Order> getOrdersByYear(int year);

    List<Order> getOrdersByMonth(int month, int year);

    Map<Integer, Long> getOrderCountByDayInMonth(int month, int year);
    String formatCurrency(double amount);

    Page<Order> getOrdersToday(Pageable pageable);

    Order findByIdAndPhone(String orderId, String phone);
}
