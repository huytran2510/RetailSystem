package com.ufm.retailsystems.controller;

import com.ufm.retailsystems.dto.cart.CartItem;
import com.ufm.retailsystems.dto.forcreate.COrder;
import com.ufm.retailsystems.dto.forlist.DailyReportDTO;
import com.ufm.retailsystems.dto.forlist.DailyRevenueDTO;
import com.ufm.retailsystems.entities.Category;
import com.ufm.retailsystems.entities.Order;
import com.ufm.retailsystems.entities.OrderDetail;
import com.ufm.retailsystems.entities.enums.Status;
import com.ufm.retailsystems.services.templates.IOrderDetailService;
import com.ufm.retailsystems.services.templates.IOrderService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);


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
            for (CartItem cartItem : cart) {
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
        return "/order/order-page";
    }

    @GetMapping("/order-tracking")
    public String orderTracking(Model model) {
        return "/order/order-tracking"; // Trang tra cứu đơn hàng
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
            int progressPercentage = 0;
            if (Objects.equals(order.getDeliveryStatus().getStatus().toString(), "SHIPPING")) {
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
    public String paymentPage(@PathVariable String orderId, Model model) {
        List<OrderDetail> orderDetails = orderDetailService.findAllByOrderId(orderId);
        if (orderDetails != null) {
            model.addAttribute("orderDetails", orderDetails);
        }
        List<String> formatPrice = orderDetails.stream()
                .map(product -> formatPriceToVND(product.getCost()))
                .collect(Collectors.toList());
        model.addAttribute("formatPrices", formatPrice);
        Order order = orderService.findByOrderId(orderId);
        if (order != null) {
            model.addAttribute("order", order);
        }
        double payment = 0;
        double totalPayment = 0;
        double priceDiscount = 0;
        for (OrderDetail item : orderDetails) {
            payment += (item.getCost() * item.getQuantity());
            totalPayment += item.getUnitPrice() * item.getQuantity();
        }
        priceDiscount = payment - totalPayment;
        model.addAttribute("payment", formatPriceToVND(payment));
        model.addAttribute("totalPayment", formatPriceToVND(totalPayment));
        model.addAttribute("priceDiscount", formatPriceToVND(priceDiscount));


        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Ngày' dd 'tháng' MM 'năm' yyyy h:mma", new Locale("vi", "VN"));
        String formattedDate = now.format(formatter);
        System.out.println(formattedDate);
        model.addAttribute("currentDate", formattedDate);
        return "/order/order-payment";
    }

    public String formatPriceToVND(double price) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        DecimalFormat decimalFormat = (DecimalFormat) formatter;
        decimalFormat.applyPattern("#,###");
        return decimalFormat.format(price) + "đ";
    }

    @Getter
    @Value("${PAY_URL}")
    private String vnp_PayUrl;

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
            session.setAttribute("orderId", order.getOrderId().toString());

            if ("vnpay".equals(cOrder.getPaymentMethod())) {
                // Redirect to VNPay payment page with order details
                DecimalFormat decimalFormat = new DecimalFormat("#");
                decimalFormat.setMaximumFractionDigits(0);
                String amount = decimalFormat.format(order.getTotalPayment());
                String bankCode = "NCB"; // You can make this dynamic if needed

                return "redirect:/payment-vnpay?amount=" + amount + "&bankCode=" + bankCode;
            } else if ("cash".equals(cOrder.getPaymentMethod())) {
                // Redirect to order page with orderId for cash on delivery
                return "redirect:/order-page/" + order.getOrderId();
            }
        }

        return "redirect:/order-page";
    }

    @GetMapping("/payment-vnpay")
    public String showPaymentPage(@RequestParam("amount") String amount,
                                  @RequestParam("bankCode") String bankCode,
                                  Model model) {
        // Add amount and bankCode to the model to use in Thymeleaf template
        model.addAttribute("amount", amount);
        model.addAttribute("bankCode", bankCode);

        // Return Thymeleaf template name
        return "/order/payment";
    }
    @GetMapping("/management-order")
    public String managementProduct(Model model) {
        List<Order> orders = orderService.findAll();
        List<String> formattedPrices = orders.stream()
                .map(product -> formatPriceToVND(product.getTotalPayment()))
                .collect(Collectors.toList());
        model.addAttribute("formattedPrices", formattedPrices);
        model.addAttribute("orders", orders);
        return "/admin/management-order";
    }

    @GetMapping("/management-order-detail")
    public String managementOrderDetail(Model model) {
        List<OrderDetail> orders = orderDetailService.findAll();
        List<String> formattedPrices = orders.stream()
                .map(product -> formatPriceToVND(product.getUnitPrice()))
                .collect(Collectors.toList());
        model.addAttribute("formattedPrices", formattedPrices);
        model.addAttribute("orderDetails", orders);
        return "/admin/management-order-details";
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
        double paymentThisYear = 0;
        double paymentThisMonth = 0;
        List<Order> ordersToday = orderService.getOrdersByDate(today);
        for (Order item : ordersToday) {
            paymentToday += (item.getTotalPayment());
        }
        List<Order> ordersThisYear = orderService.getOrdersByYear(currentYear);
        for (Order item : ordersThisYear) {
            paymentThisYear += (item.getTotalPayment());
        }
        List<Order> ordersThisMonth = orderService.getOrdersByMonth(currentMonth, currentYear);
        for (Order item : ordersThisMonth) {
            paymentThisMonth += (item.getTotalPayment());
        }

        List<DailyRevenueDTO> revenues = orderService.getMonthlyRevenue(currentYear, currentMonth);

        model.addAttribute("revenues", revenues);
        model.addAttribute("year", currentYear);
        model.addAttribute("month", currentMonth);

        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
        System.out.println(startOfWeek);
        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);
        System.out.println(endOfWeek);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedStartOfWeek = startOfWeek.format(formatter);
        String formattedEndOfWeek = endOfWeek.format(formatter);
        model.addAttribute("startOfWeek", formattedStartOfWeek);
        model.addAttribute("endOfWeek", formattedEndOfWeek);

        model.addAttribute("paymentToday", formatPriceToVND(paymentToday));
        model.addAttribute("paymentThisYear", formatPriceToVND(paymentThisYear));
        model.addAttribute("paymentThisMonth", formatPriceToVND(paymentThisMonth));

        model.addAttribute("ordersByDayInMonth", orderCounts);
        model.addAttribute("daysWithOrders", daysWithOrders);

        model.addAttribute("ordersToday", formatOrders(ordersToday));
        model.addAttribute("ordersThisYear", formatOrders(ordersThisYear));

        model.addAttribute("ordersThisMonth", formatOrders(ordersThisMonth));
        model.addAttribute("selectedYear", currentYear);
        model.addAttribute("selectedMonth", currentMonth);

        return "/admin/dashboard";
    }

    @GetMapping("/daily")
    public ResponseEntity<List<DailyReportDTO>> getDailyReportForCurrentWeek() {
        try {
            List<DailyReportDTO> dailyReport = orderService.getDailyReportForCurrentWeek();
            return new ResponseEntity<>(dailyReport, HttpStatus.OK);
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            // Return an error response with a meaningful message
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<Order> formatOrders(List<Order> orders) {
        return orders.stream().map(order -> {
            order.setTotalPaymentFormatted(orderService.formatCurrency(order.getTotalPayment()));
            return order;
        }).collect(Collectors.toList());
    }

    @PostMapping("/update-order")
    public String updateStatus(@RequestParam("orderId") String orderId, @RequestParam("deliveryStatus") Status status, RedirectAttributes redirectAttributes) {
        try {
            orderService.update(orderId, status);
            redirectAttributes.addFlashAttribute("result", "Cập nhật thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("result", "Cập nhật thất bại");
        }
        return "redirect:/management-order";
    }

}
