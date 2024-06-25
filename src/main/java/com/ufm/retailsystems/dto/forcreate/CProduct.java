package com.ufm.retailsystems.dto.forcreate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CProduct {
    private Long id;
    private String productName;
    private Double unitPrice;
    private Integer unitsInStock;
    private Boolean discontinued;
    private Long categoryId;
    private String description;
//    private String imageUrl;
}
