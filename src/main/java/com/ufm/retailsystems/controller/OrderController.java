package com.ufm.retailsystems.controller;

import com.ufm.retailsystems.dto.cart.CartItem;
import com.ufm.retailsystems.dto.forcreate.COrder;
import com.ufm.retailsystems.entities.Order;
import com.ufm.retailsystems.entities.OrderDetail;
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
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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
    public String orderTracking(Model model) {
        return "product/order-tracking"; // Trang tra cứu đơn hàng
    }

    @GetMapping("/order-tracking/load")
    public String loadOrderTracking(Model model,
                                    @RequestParam("orderId") String orderId,
                                    @RequestParam("phone") String phone) {
        if (orderId == null || phone == null) {
            model.addAttribute("error", "Thiếu thông tin mã đơn hàng hoặc số điện thoại đặt hàng");
            return "order/order-tracking";
        }

        Order order = orderService.findByIdAndPhone(orderId, phone);
        if (order == null) {
            model.addAttribute("error", "Không tìm thấy đơn hàng phù hợp");
        } else {
            int progressPercentage =0;
            if(Objects.equals(order.getDeliveryStatus().getStatus().toString(), "SHIPPING")) {
                progressPercentage = 60;
            } else if (Objects.equals(order.getDeliveryStatus().getStatus().toString(), "PENDING")) {
                progressPercentage = 30;
            } else {
                progressPercentage = 100;
            }
            model.addAttribute("progressPercentage", progressPercentage);
            model.addAttribute("order", order);
        }
        return "order/order-tracking";
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
        return "/order/order-payment";
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
            return "/order/order-page";
        }
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        Order order = orderService.saveOrder(cOrder, cart);
        if (order != null) {
            redirectAttributes.addFlashAttribute("orderSuccess", true);
            session.removeAttribute("cart");
            return "redirect:/order-page/"+order.getOrderId();
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
        return "/admin/management-order";
    }

    @GetMapping("/dashboard")
    public String getDashboard(@RequestParam(value = "month", required = false) Integer month,
                               @RequestParam(value = "year", required = false) Integer year,
                               Model model) {
        LocalDate today = LocalDate.now();
        int currentYear = year != null ? year : Year.now().getValue();
        int currentMonth = month != null ? month : YearMonth.now().getMonthValue();

        Map<Integer, Long> ordersByDayInMonth = orderService.getOrderCountByDayInMonth(currentMonth, currentYear);
        List<Integer> daysWithOrders = ordersByDayInMonth.keySet().stream().sorted().collect(Collectors.toList());
        List<Long> orderCounts = daysWithOrders.stream().map(ordersByDayInMonth::get).collect(Collectors.toList());
        double paymentToday = 0;
        double paymentThisYear= 0;
        double paymentThisMonth = 0;
        List<Order> ordersToday = orderService.getOrdersByDate(today);
        for(Order item : ordersToday) {
            paymentToday += (item.getTotalPayment());
        }
        List<Order> ordersThisYear = orderService.getOrdersByYear(currentYear);
        for(Order item : ordersThisYear) {
            paymentThisYear += (item.getTotalPayment());
        }
        List<Order> ordersThisMonth = orderService.getOrdersByMonth(currentMonth, currentYear);
        for(Order item : ordersThisMonth) {
            paymentThisMonth += (item.getTotalPayment());
        }

        model.addAttribute("paymentToday",formatPriceToVND(paymentToday));
        model.addAttribute("paymentThisYear",formatPriceToVND(paymentThisYear));
        model.addAttribute("paymentThisMonth",formatPriceToVND(paymentThisMonth));

        model.addAttribute("ordersByDayInMonth", orderCounts);
        model.addAttribute("daysWithOrders", daysWithOrders);
        model.addAttribute("ordersToday", formatOrders(ordersToday));
        model.addAttribute("ordersThisYear", formatOrders(ordersThisYear));
        model.addAttribute("ordersThisMonth", formatOrders(ordersThisMonth));
        model.addAttribute("selectedYear", currentYear);
        model.addAttribute("selectedMonth", currentMonth);

        return "/admin/dashboard";
    }
//    @GetMapping("/dashboard/orders")
//    public String showOrdersToday(@RequestParam(defaultValue = "0") int page,
//                                  @RequestParam(defaultValue = "10") int size,
//                                  Model model) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Order> ordersToday = orderService.getOrdersToday(pageable);
//        model.addAttribute("ordersToday", ordersToday);
//        return "dashboard";
//    }
    private List<Order> formatOrders(List<Order> orders) {
        return orders.stream().map(order -> {
            order.setTotalPaymentFormatted(orderService.formatCurrency(order.getTotalPayment()));
            return order;
        }).collect(Collectors.toList());
    }
}
