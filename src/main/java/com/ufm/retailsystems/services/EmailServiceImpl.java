package com.ufm.retailsystems.services;


import com.ufm.retailsystems.dto.cart.CartItem;
import com.ufm.retailsystems.repositories.UserRepository;
import com.ufm.retailsystems.services.templates.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements IEmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void sendSuccessRegister(String toEmail, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("huy251003@gmail.com");
        message.setTo(toEmail);
        String htmlContent = ""
                + "Đăng ký thành công. "
                + "Xin chúc mừng! Bạn đã đăng ký thành công. "
                + "Mật khẩu mới của bạn là: " + newPassword + " "
                + "Cảm ơn bạn!";
        message.setSubject("Đăng ký tài khoản thành công");
        message.setText(htmlContent);
        try {
            emailSender.send(message);
            System.out.println("Email sent successfully.");
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }

    public void sendOrderConfirmationEmail(String toEmail, String orderId, String customerName, String phone, List<CartItem> itemList) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("huy251003@gmail.com");
        message.setSubject("Xác nhận thanh toán thành công");
        message.setTo(toEmail);
        String messageSend = String.format("Kính gửi %s,\n\nĐơn hàng của bạn đã được đặt thành công.\nMã đơn hàng: %s\nTên khách hàng: %s\nSố điện thoại: %s\n\nXin cảm ơn quý khách đã mua sắm tại cửa hàng của chúng tôi!", customerName, orderId, customerName, phone);
        message.setText(messageSend);
        List<String> itemListing = itemList.stream()
                .map(item -> String.format("- %s", item.getProductName()))
                .collect(Collectors.toList());

        String itemListingFormatted = String.join("\n", itemListing);
        messageSend = String.format("%s\n\nBạn đã đặt mua những sản phẩm sau:\n\n%s\n\n", messageSend, itemListingFormatted);
        message.setText(messageSend);
        try {
            emailSender.send(message);
            System.out.println("Send email success fully.");
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
        // Assuming you have a method to send emails
    }

}
