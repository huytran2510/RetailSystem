package com.ufm.retailsystems.services.templates;

import com.ufm.retailsystems.dto.forcreate.COrder;
import com.ufm.retailsystems.entities.Order;

public interface IOrderService {
    Order saveOrder(COrder cOrder);
}
