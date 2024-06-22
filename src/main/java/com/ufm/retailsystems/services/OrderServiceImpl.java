package com.ufm.retailsystems.services;

import com.ufm.retailsystems.dto.cart.CartItem;
import com.ufm.retailsystems.dto.forcreate.COrder;
import com.ufm.retailsystems.entities.*;
import com.ufm.retailsystems.repositories.*;
import com.ufm.retailsystems.services.templates.IEmailService;
import com.ufm.retailsystems.services.templates.IOrderDetailService;
import com.ufm.retailsystems.services.templates.IOrderService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    @Autowired
    private IOrderDetailService orderDetailService;

    @Autowired
    private IEmailService emailService;


    @Override
    public Order saveOrder(COrder cOrder, List<CartItem> itemList) {
        double totalPrice = 0;
        for(CartItem item : itemList) {
            totalPrice += (item.getPriceDiscount()*item.getQuantity());
        }
        Order order = new Order();
        order.setOrderId(generateOrderId());
        order.setOrderDate(LocalDate.now());
        order.setRequiredDate(LocalDate.now());
        order.setShipAddress(cOrder.getShipAddress());
        order.setShipName(cOrder.getFirstName() + "" + cOrder.getLastName());
        order.setShipPhone(cOrder.getShipPhone());
        order.setShippedDate(cOrder.getShippedDate());
        order.setTotalPayment(totalPrice);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (!username.equals("anonymousUser")) {
            Customer customer = customerRepository.findByUsername(username);
            order.setCustomer(customer);
        }

        DeliveryStatus deliveryStatus = deliveryStatusRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Invalid status ID"));
        order.setDeliveryStatus(deliveryStatus);

        Shipper shipper = shipperRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Invalid shipper ID"));
        order.setShipper(shipper);
        order.setEmail(cOrder.getEmail());
        // Save the entity to the database
        orderRepository.save(order);

        for (CartItem item : itemList) {
            orderDetailService.save(item, order);
        }
        System.out.println(cOrder.getEmail());
        emailService.sendOrderConfirmationEmail(cOrder.getEmail(), order.getOrderId(), order.getShipName(), order.getShipPhone(), itemList);
        return order;
    }

    public String generateOrderId() {
        LocalDateTime localDateTime = LocalDateTime.now();
        String dateStr = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timeStr = localDateTime.format(DateTimeFormatter.ofPattern("HHmmss"));
        String orderId = "HD" + dateStr + "" + timeStr;
        return orderId;
    }

    public List<Order> findAll(){
       return orderRepository.findAll();
    }

    public Order findByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }
}
