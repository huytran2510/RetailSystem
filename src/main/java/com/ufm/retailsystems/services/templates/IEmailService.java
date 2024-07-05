package com.ufm.retailsystems.services.templates;

import com.ufm.retailsystems.dto.cart.CartItem;

import java.util.List;

public interface IEmailService {
     void sendSuccessRegister(String toEmail, String newPassword);
    void sendOrderConfirmationEmail(String toEmail, String orderId, String customerName, String phone, List<CartItem> itemList);
}
