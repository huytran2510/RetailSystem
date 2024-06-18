package com.ufm.retailsystems.controller;

import com.ufm.retailsystems.dto.cart.CartItem;
import com.ufm.retailsystems.entities.Product;
import com.ufm.retailsystems.services.templates.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@CrossOrigin(origins = "http://localhost:9091")
public class CartController {
    @Autowired
    private IProductService productService;


    @PostMapping("/add-to-cart")
    @ResponseBody // Chú thích này cũng có thể thay thế bằng @RestController ở cấp lớp
    public ResponseEntity<Object> addToCart(@RequestParam("productId") Long productId, HttpSession session) {
        Optional<Product> optionalProduct = productService.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
                session.setAttribute("cart", cart);
            }

            boolean productExistsInCart = false;
            for (CartItem item : cart) {
                if (item.getProductId().equals(productId)) {
                    // If the product exists in the cart, increment the quantity by 1
                    item.setQuantity(item.getQuantity() + 1);
                    productExistsInCart = true;
                    break;
                }
            }
            if (!productExistsInCart) {
                CartItem cartItem = new CartItem();
                cartItem.setProductId(product.getProductId());
                cartItem.setPrice(product.getUnitPrice());
                cartItem.setProductName(product.getProductName());
                cartItem.setQuantity(1);
                cartItem.setImgUrl(product.getSingleImgUrl());
                cart.add(cartItem);
            }

            session.setAttribute("cart", cart);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Product added to cart successfully");
            return ResponseEntity.ok(responseData);
        } else {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", false);
            responseData.put("message", "Product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
        }
    }

    @GetMapping("/cart")
    public String cart(Model model, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null && !cart.isEmpty()) {
            List<String> formattedPrices = cart.stream()
                    .map(product -> formatPriceToVND(product.getPrice()))
                    .collect(Collectors.toList());
            model.addAttribute("carts", cart);
            model.addAttribute("formattedPrices", formattedPrices);
            model.addAttribute("isCartEmpty", false); // Cart is not empty
        } else {
            model.addAttribute("isCartEmpty", true); // Cart is empty
        }
        return "cart";
    }


    public String formatPriceToVND(double price) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        DecimalFormat decimalFormat = (DecimalFormat) formatter;
        decimalFormat.applyPattern("#,###");
        return decimalFormat.format(price) + " đ";
    }

//    @GetMapping("/quantity-product")
//    public String cartQuantity(Model model, HttpSession session) {
//        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
//        if (cart == null) {
//            cart = new ArrayList<>(); // Initialize if null
//        }
//        int n = cart.size();
//        model.addAttribute("quantity", n);
//        System.out.println("Cart size: " + n); // Debugging line
//        return "layout/header";
//
//    }

    @PostMapping("/add-quantity")
    public String updateQuantityCart(@RequestParam Long productId, @RequestParam Integer quantity, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        int updatedQuantity = 0;
        for(CartItem item : cart) {
            if (Objects.equals(item.getProductId(), productId)) {
                item.setQuantity(quantity);
                updatedQuantity = item.getQuantity();
            }
        }
        session.setAttribute("cart", cart); // Cập nhật lại session cart sau khi thay đổi

        // Ghi log để kiểm tra
        System.out.println("Updated quantity for productId " + productId + ": " + updatedQuantity);

        return "redirect:/cart";
    }
}
