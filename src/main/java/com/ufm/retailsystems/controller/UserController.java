package com.ufm.retailsystems.controller;


import com.ufm.retailsystems.dto.forcreate.CCustomer;
import com.ufm.retailsystems.services.templates.ISecurityService;
import com.ufm.retailsystems.services.templates.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
        model.addAttribute("user", new CCustomer());
        return "register";
    }

    @PostMapping("/register")
    public String registration(@ModelAttribute("user") @Valid CCustomer cUser, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        userService.save(cUser);

        securityService.autoLogin(cUser.getUsername(), cUser.getPassword());
        return "redirect:/blog/create";
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login-form";
    }

    @GetMapping("/logout")
    public String logout(Model model){
        return "home-page";
    }
}
