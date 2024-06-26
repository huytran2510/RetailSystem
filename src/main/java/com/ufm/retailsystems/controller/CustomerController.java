package com.ufm.retailsystems.controller;


import com.ufm.retailsystems.dto.forcreate.CCustomer;
import com.ufm.retailsystems.dto.login.UserLoginDTO;
import com.ufm.retailsystems.services.templates.ICustomerService;
import com.ufm.retailsystems.services.templates.ISecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CustomerController {
    @Autowired
    private ICustomerService customerService;

    @Autowired
    private ISecurityService securityService;
    @GetMapping("/customer/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "customer-login-form";
    }

    @PostMapping("/customer/login")
    public String login(@ModelAttribute("customer") UserLoginDTO userLoginDTO, BindingResult bindingResult, Model model) {
        boolean loginSuccess = customerService.login(userLoginDTO.getUsername(), userLoginDTO.getPassword());

        if (!loginSuccess) {
            model.addAttribute("loginError", "Invalid username or password");
            return "customer-login-form";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getName());
        System.out.println("Successful login");

        return "redirect:/mobile";
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> register(@Valid @ModelAttribute("customer") CCustomer cCustomer, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(Map.of("success", false, "errors", errors));
        }
        customerService.add(cCustomer);
        customerService.login(cCustomer.getUsername(),cCustomer.getPassword());
        // Process the registration logic here
        return ResponseEntity.ok(Map.of("success", true));
    }
}
