package com.ufm.retailsystems.controller;


import com.ufm.retailsystems.dto.forcreate.CCustomer;
import com.ufm.retailsystems.dto.forupdate.UCustomer;
import com.ufm.retailsystems.dto.login.UserLoginDTO;
import com.ufm.retailsystems.entities.Customer;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api")
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
            model.addAttribute("loginError", "Sai mật khẩu hoặc tài khoản");
            return "customer-login-form";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getName());
        System.out.println("Successful login");

        return "redirect:/mobile";
    }

    @PostMapping("/customer/register")
    public String register(@Valid @ModelAttribute("customer") CCustomer cCustomer, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            model.addAttribute("errors", errors);
            return "register"; // Return to the registration form with errors
        }

        try {
            customerService.add(cCustomer);
            customerService.login(cCustomer.getUsername(), cCustomer.getPassword());
            model.addAttribute("result", true);
            return "redirect:/mobile"; // Redirect to a success page
        } catch (Exception e) {
            model.addAttribute("result", "Username hoặc email đã tồn tại");
            return "register"; // Return to the registration form with error message
        }
    }

    @GetMapping("/profile")
    public String showForm(Model model, HttpServletRequest request) {
        Authentication userDetails = SecurityContextHolder.getContext().getAuthentication();
        String username = userDetails.getName();
        Customer customer = customerService.findByUsername(username);
        if (customer != null) {
            model.addAttribute("profile", customer);
            return "/user/personal-profile";
        }
        else {
            String currentUrl = request.getRequestURL().toString();
            return "redirect:" + currentUrl;
        }
    }



    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute("profile") Customer customer, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Update profile for the current user
        customer.setUsername(currentUsername); // Ensure username remains unchanged
        customerService.updateProfile(customer);

        redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin thành công");
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        System.out.println(currentUsername);
        System.out.println("change-password");
        // Change password for the current user
        boolean passwordChanged = customerService.changePassword(currentUsername, currentPassword, newPassword, confirmPassword);
        System.out.println(passwordChanged);
        if (passwordChanged) {
            redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công!");
        } else {
            redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu không thành công. Vui lòng kiểm tra lại mật khẩu hiện tại");
        }

        return "redirect:/profile";
    }
}
