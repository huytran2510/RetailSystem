package com.ufm.retailsystems.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    @Column(nullable = false)
    private LocalDate orderDate;
    @Column(nullable = false)
    private LocalDate requiredDate;
    @Column(nullable = false)
    private LocalDate shippedDate;
    @Column(nullable = false)
    private String shipName;
    @Column(nullable = false)
    private String shipAddress;
    @Column(nullable = false)
    private String shipPhone;
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private double totalPayment;

    @OneToMany(mappedBy = "order")
    private Set<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "order")
    private Set<Payment> payments;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private DeliveryStatus deliveryStatus;

    @ManyToOne
    private Shipper shipper;

    @Transient
    private String totalPaymentFormatted;

    // Getters and Setters

    public String getTotalPaymentFormatted() {
        return totalPaymentFormatted;
    }

    public void setTotalPaymentFormatted(String totalPaymentFormatted) {
        this.totalPaymentFormatted = totalPaymentFormatted;
    }
}
