package com.ufm.retailsystems.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "orders")
public class Order {

    @Id
    private String orderId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User user;

    private LocalDate orderDate;
    private LocalDate requiredDate;
    private LocalDate shippedDate;
    private String shipName;
    private String shipAddress;
    private String shipPhone;

    @OneToMany(mappedBy = "order")
    private Set<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "order")
    private Set<Payment> payments;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private DeliveryStatus deliveryStatus;

    @ManyToOne
    private Shipper shipper;

    // Getters and Setters
}
