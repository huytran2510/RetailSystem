package com.ufm.retailsystems.services;


import com.ufm.retailsystems.repositories.UserRepository;
import com.ufm.retailsystems.services.templates.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements IEmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void sendPasswordResetEmail(String toEmail, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("huy251003@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Password Reset");
        message.setText("Your new password is: " + newPassword);
        try {
            emailSender.send(message);
            System.out.println("Password reset email sent successfully.");
        } catch (Exception e) {
            System.err.println("Error sending password reset email: " + e.getMessage());
        }
    }
}
