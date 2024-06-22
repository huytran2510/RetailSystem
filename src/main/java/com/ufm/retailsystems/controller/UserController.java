package com.ufm.retailsystems.controller;


import com.ufm.retailsystems.dto.forcreate.CCustomer;
import com.ufm.retailsystems.dto.login.UserLoginDTO;
import com.ufm.retailsystems.services.templates.ISecurityService;
import com.ufm.retailsystems.services.templates.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ISecurityService securityService;

    @GetMapping("/register")
    public String registration(Model model) {
        model.addAttribute("customer", new CCustomer());
        return "register";
    }



    @GetMapping("/login")
    public String loginPage(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login-form";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("customer") UserLoginDTO userLoginDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // Return to login page with errors
            return "login-form";
        }

        try {
            securityService.autoLogin(userLoginDTO.getUsername(), userLoginDTO.getPassword());
            Authentication userDetails = SecurityContextHolder.getContext().getAuthentication();
            System.out.println(userDetails.getName());
            System.out.println("success login");
            return "redirect:/management-product";
        } catch (AuthenticationException e) {
            // Handle authentication failure
            model.addAttribute("error", "Invalid username or password.");
            return "login-form";
        }
    }


    @GetMapping("/logout")
    public String logout(Model model){
        return "home-page";
    }
}
