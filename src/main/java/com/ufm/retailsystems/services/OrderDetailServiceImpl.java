package com.ufm.retailsystems.services;

import com.ufm.retailsystems.dto.cart.CartItem;
import com.ufm.retailsystems.entities.Order;
import com.ufm.retailsystems.entities.OrderDetail;
import com.ufm.retailsystems.entities.Product;
import com.ufm.retailsystems.repositories.OrderDetailRepository;
import com.ufm.retailsystems.repositories.OrderRepository;
import com.ufm.retailsystems.repositories.ProductRepository;
import com.ufm.retailsystems.services.templates.IOrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrderDetailServiceImpl implements IOrderDetailService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public void save(CartItem item, Order order) {
        Product product = productRepository.findProductByProductId(item.getProductId());
        // Trừ quantity từ unitsInStock của product
        int newUnitsInStock = product.getUnitsInStock() - item.getQuantity();
        product.setUnitsInStock(newUnitsInStock);
        // Lưu product với số lượng mới
        productRepository.save(product);
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        orderDetail.setDiscount(product.getDiscount().getDiscountPercent());
        orderDetail.setQuantity(item.getQuantity());
        orderDetail.setCost(item.getPrice());
        orderDetail.setUnitPrice(item.getPriceDiscount());
        orderDetailRepository.save(orderDetail);
    }

    public List<OrderDetail> findAllByOrderId(String orderId){
        return orderDetailRepository.findAllByOrderId(orderId);
    }

    public List<OrderDetail> findAll() {
        return orderDetailRepository.findAll();
    }
}
