package com.ufm.retailsystems.services.templates;

import com.ufm.retailsystems.dto.cart.CartItem;
import com.ufm.retailsystems.dto.forcreate.COrder;
import com.ufm.retailsystems.entities.Order;

import java.util.List;

public interface IOrderService {
    Order saveOrder(COrder cOrder, List<CartItem> itemList);
    Order findByOrderId(String orderId);

    List<Order> findAll();
}
