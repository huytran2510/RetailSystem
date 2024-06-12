package com.ufm.retailsystems.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long discountId;

    @OneToMany(mappedBy = "discount")
    private Set<Product> products;

    private String discountDescription;
    private Double discountPercent;
    private LocalDate startDate;
    private LocalDate endDate;
}
