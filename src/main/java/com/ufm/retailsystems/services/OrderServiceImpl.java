package com.ufm.retailsystems.services;

import com.ufm.retailsystems.dto.forcreate.COrder;
import com.ufm.retailsystems.entities.*;
import com.ufm.retailsystems.repositories.*;
import com.ufm.retailsystems.services.templates.IOrderService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DeliveryStatusRepository deliveryStatusRepository;

    @Autowired
    private ShipperRepository shipperRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public Order saveOrder(COrder cOrder) {
        Order order = new Order();
        order.setOrderId(generateOrderId());
        order.setOrderDate(LocalDate.now());
        order.setRequiredDate(LocalDate.now());
        order.setShipAddress(cOrder.getShipAddress());
        order.setShipName(cOrder.getFirstName() + "" + cOrder.getLastName());
        order.setShipPhone(cOrder.getShipPhone());
        order.setShippedDate(cOrder.getShippedDate());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (username != null) {
            Customer customer = customerRepository.findByUsername(username);
            order.setCustomer(customer);
        }

        DeliveryStatus deliveryStatus = deliveryStatusRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Invalid status ID"));
        order.setDeliveryStatus(deliveryStatus);

        Shipper shipper = shipperRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Invalid shipper ID"));
        order.setShipper(shipper);

        // Save the entity to the database
        return orderRepository.save(order);
    }

    public String generateOrderId(){
        LocalDateTime localDateTime = LocalDateTime.now();
        String result = "HD" + localDateTime.toString();
        return result;
    }
}
