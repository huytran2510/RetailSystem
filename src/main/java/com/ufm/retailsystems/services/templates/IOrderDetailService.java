package com.ufm.retailsystems.services.templates;

import com.ufm.retailsystems.dto.cart.CartItem;
import com.ufm.retailsystems.entities.Order;

public interface IOrderDetailService {
    void save(CartItem item, Order order);
}
