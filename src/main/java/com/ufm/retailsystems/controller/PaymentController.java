package com.ufm.retailsystems.controller;

import com.ufm.retailsystems.dto.payment.PaymentDTO;
import com.ufm.retailsystems.dto.reponse.ResponseObject;
import com.ufm.retailsystems.entities.Order;
import com.ufm.retailsystems.entities.OrderDetail;
import com.ufm.retailsystems.services.PaymentService;
import com.ufm.retailsystems.services.templates.IOrderDetailService;
import com.ufm.retailsystems.services.templates.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

//@RestController
//@RequestMapping("${spring.application.api-prefix}/payment")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {
    private final PaymentService paymentService;
    @Autowired
    private IOrderService orderService;

    @Autowired
    private IOrderDetailService orderDetailService;
    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request, HttpSession session) {
        return new ResponseObject<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request,session));
    }
    @GetMapping("/payment/vn-pay-callback")
    public String payCallbackHandler(HttpServletRequest request, HttpSession session, Model model) {
        String status = request.getParameter("vnp_ResponseCode");
        String orderId = (String) session.getAttribute("orderId");

        if ("00".equals(status)) {
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
//            return "/order-page/" + orderId;
        } else {
            // Redirect to the order page with orderId and a failure status
            return "forward:/order-page/" + orderId + "?status=failed";
        }
    }
    public String formatPriceToVND(double price) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        DecimalFormat decimalFormat = (DecimalFormat) formatter;
        decimalFormat.applyPattern("#,###");
        return decimalFormat.format(price) + "đ";
    }

}