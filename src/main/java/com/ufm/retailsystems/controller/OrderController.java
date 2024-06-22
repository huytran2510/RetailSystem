package com.ufm.retailsystems.controller;

import com.ufm.retailsystems.dto.cart.CartItem;
import com.ufm.retailsystems.dto.forcreate.COrder;
import com.ufm.retailsystems.dto.login.UserLoginDTO;
import com.ufm.retailsystems.entities.Order;
import com.ufm.retailsystems.entities.OrderDetail;
import com.ufm.retailsystems.entities.Product;
import com.ufm.retailsystems.services.templates.IOrderDetailService;
import com.ufm.retailsystems.services.templates.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @Autowired
    private IOrderDetailService orderDetailService;
    @GetMapping("/order-page")
    public String orderPage(Model model, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        double payment = 0;
        if (cart != null && !cart.isEmpty()) {
            List<String> formattedPrices = cart.stream()
                    .map(product -> formatPriceToVND(product.getPrice()))
                    .collect(Collectors.toList());
            for(CartItem cartItem : cart) {
                payment += (cartItem.getPriceDiscount() * cartItem.getQuantity());
            }
            List<String> formatPriceDiscount = cart.stream()
                    .map(product -> formatPriceToVND(product.getPriceDiscount()))
                    .collect(Collectors.toList());
            model.addAttribute("formatPriceDiscount", formatPriceDiscount);
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

    @GetMapping("/order-tracking")
    public String orderTracking(Model model, HttpSession session) {
//        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
//        double payment = 0;
//        if (cart != null && !cart.isEmpty()) {
//            List<String> formattedPrices = cart.stream()
//                    .map(product -> formatPriceToVND(product.getPrice()))
//                    .collect(Collectors.toList());
//            for(CartItem cartItem : cart) {
//                payment += (cartItem.getPriceDiscount() * cartItem.getQuantity());
//            }
//            List<String> formatPriceDiscount = cart.stream()
//                    .map(product -> formatPriceToVND(product.getPriceDiscount()))
//                    .collect(Collectors.toList());
//            model.addAttribute("formatPriceDiscount", formatPriceDiscount);
//            model.addAttribute("payment", formatPriceToVND(payment));
//            model.addAttribute("carts", cart);
//            model.addAttribute("order", new COrder());
//            model.addAttribute("formattedPrices", formattedPrices);
//            model.addAttribute("isCartEmpty", false); // Cart is not empty
//        } else {
//            model.addAttribute("isCartEmpty", true); // Cart is empty
//        }
        return "/product/order-tracking";
    }

    @GetMapping("/order-page/{orderId}")
    public String paymentPage(@PathVariable String orderId , Model model) {
        List<OrderDetail> orderDetails = orderDetailService.findAllByOrderId(orderId);
        if (orderDetails != null) {
            model.addAttribute("orderDetails", orderDetails);
        }
        List<String> formatPrice = orderDetails.stream()
                .map(product -> formatPriceToVND(product.getCost()))
                .collect(Collectors.toList());
        model.addAttribute("formatPrices", formatPrice);
        Order order = orderService.findByOrderId(orderId);
        if (order!= null) {
            model.addAttribute("order",order);
        }
        double payment = 0;
        double totalPayment =0;
        double priceDiscount =0;
        for(OrderDetail item : orderDetails) {
            payment += (item.getCost()*item.getQuantity());
            totalPayment += item.getUnitPrice();
        }
        priceDiscount = payment - totalPayment;
        model.addAttribute("payment",formatPriceToVND(payment));
        model.addAttribute("totalPayment",formatPriceToVND(totalPayment));
        model.addAttribute("priceDiscount",formatPriceToVND(priceDiscount));
        return "order-payment";
    }

    public String formatPriceToVND(double price) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        DecimalFormat decimalFormat = (DecimalFormat) formatter;
        decimalFormat.applyPattern("#,###");
        return decimalFormat.format(price) + "đ";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute("order") COrder cOrder, BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "order-page";
        }
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        Order order = orderService.saveOrder(cOrder, cart);
        if (order != null) {
            redirectAttributes.addFlashAttribute("orderSuccess", true);
            return "redirect:/order-page";
        }
        return "redirect:/order-page";
    }

    @GetMapping("/management-order")
    public String managementProduct(Model model) {
        List<Order> orders = orderService.findAll();
        List<String> formattedPrices = orders.stream()
                .map(product -> formatPriceToVND(product.getTotalPayment()))
                .collect(Collectors.toList());
        model.addAttribute("formattedPrices", formattedPrices);
        model.addAttribute("orders" , orders);
        return "management-order";
    }
}
