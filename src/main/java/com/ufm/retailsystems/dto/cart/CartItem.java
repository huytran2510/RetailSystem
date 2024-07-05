package com.ufm.retailsystems.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartItem {
    private Long productId;
    private String productName;
    private int quantity;
    private double price;
    private double priceDiscount;
    private double discount;

    private String imgUrl;
}