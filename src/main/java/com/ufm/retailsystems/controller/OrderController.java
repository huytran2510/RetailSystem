package com.ufm.retailsystems.controller;

import com.ufm.retailsystems.dto.cart.CartItem;
import com.ufm.retailsystems.dto.forcreate.COrder;
import com.ufm.retailsystems.dto.login.UserLoginDTO;
import com.ufm.retailsystems.entities.Order;
import com.ufm.retailsystems.services.templates.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
public class OrderController {

    @Autowired
    private IOrderService orderService;
    @GetMapping("/payment-page")
    public String cart(Model model, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        double payment = 0;
        if (cart != null && !cart.isEmpty()) {
            List<String> formattedPrices = cart.stream()
                    .map(product -> formatPriceToVND(product.getPrice()))
                    .collect(Collectors.toList());
            for(CartItem cartItem : cart) {
                payment += (cartItem.getPrice() * cartItem.getQuantity());
            }
            model.addAttribute("payment", formatPriceToVND(payment));
            model.addAttribute("carts", cart);
            model.addAttribute("order", new COrder());
            model.addAttribute("formattedPrices", formattedPrices);
            model.addAttribute("isCartEmpty", false); // Cart is not empty
        } else {
            model.addAttribute("isCartEmpty", true); // Cart is empty
        }
        return "order-page";
    }

    public String formatPriceToVND(double price) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        DecimalFormat decimalFormat = (DecimalFormat) formatter;
        decimalFormat.applyPattern("#,###");
        return decimalFormat.format(price) + "đ";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute("order") COrder cOrder, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {

            return "order-page";
        }

        Order order = orderService.saveOrder(cOrder);
        if (order != null) {
            return "order-page";
        }
        return "order-page";
    }
}
