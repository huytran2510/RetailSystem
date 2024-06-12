package com.ufm.retailsystems.controller;


import com.ufm.retailsystems.dto.login.UserLoginDTO;
import com.ufm.retailsystems.services.templates.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CustomerController {
    @Autowired
    private ICustomerService customerService;
    @GetMapping("/customer/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "customer-login-form";
    }

    @PostMapping("/customer/login")
    public String login(@ModelAttribute("customer")UserLoginDTO userLoginDTO, BindingResult bindingResult,Model model) {
        boolean n = customerService.login(userLoginDTO.getUsername(), userLoginDTO.getPassword());

        if(n == false) {
            System.out.println("fail to check login");
            model.addAttribute("loginError", "Invalid username or password");
            return "customer-login-form";
        }
        Authentication userDetails = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(userDetails.getName());
        System.out.println("success login");
        return "customer-login-form";
    }
}
