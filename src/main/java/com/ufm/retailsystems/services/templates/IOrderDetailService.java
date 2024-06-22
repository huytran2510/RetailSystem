package com.ufm.retailsystems.services.templates;

import com.ufm.retailsystems.dto.cart.CartItem;
import com.ufm.retailsystems.entities.Order;
import com.ufm.retailsystems.entities.OrderDetail;

import java.util.List;

public interface IOrderDetailService {
    void save(CartItem item, Order order);
    List<OrderDetail> findAllByOrderId(String orderId);
}
