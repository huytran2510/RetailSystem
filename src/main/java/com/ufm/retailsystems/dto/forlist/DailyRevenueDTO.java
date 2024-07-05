package com.ufm.retailsystems.dto.forlist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class DailyRevenueDTO {
    private LocalDate date;
    private double totalPayment;
    public DailyRevenueDTO(LocalDate date, double totalPayment) {
        this.date = date;
        this.totalPayment = totalPayment;
    }
}
