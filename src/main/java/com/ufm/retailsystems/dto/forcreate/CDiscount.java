package com.ufm.retailsystems.dto.forcreate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CDiscount {
    public Long discountId;
    public String description;
    public Double percent;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDay;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDay;
}
