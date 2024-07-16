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
@RequestMapping("/api")
public class CartController {
    @Autowired
    private IProductService productService;


    @PostMapping("/add-to-cart")
    @ResponseBody
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
                cartItem.setDiscount(product.getDiscount().getDiscountPercent());
                cartItem.setPriceDiscount(product.getUnitPrice() - product.getDiscount().getDiscountPercent() * product.getUnitPrice());
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
        double payment = 0;
        if (cart != null && !cart.isEmpty()) {
            List<String> formattedPrices = cart.stream()
                    .map(product -> formatPriceToVND(product.getPrice()))
                    .collect(Collectors.toList());
            List<String> formattedPricesDiscount = cart.stream()
                    .map(product -> formatPriceToVND(product.getPriceDiscount()))
                    .collect(Collectors.toList());
            for (CartItem cartItem : cart) {
                payment += (cartItem.getPriceDiscount() * cartItem.getQuantity());
            }
            model.addAttribute("payment", formatPriceToVND(payment));
            model.addAttribute("formattedPricesDiscount", formattedPricesDiscount);
            model.addAttribute("carts", cart);
            model.addAttribute("formattedPrices", formattedPrices);
            model.addAttribute("isCartEmpty", false); // Cart is not empty
        } else {
            model.addAttribute("isCartEmpty", true); // Cart is empty
        }
        return "/order/cart";
    }


    public String formatPriceToVND(double price) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        DecimalFormat decimalFormat = (DecimalFormat) formatter;
        decimalFormat.applyPattern("#,###");
        return decimalFormat.format(price) + "đ";
    }

//    @PostMapping("/add-quantity")
    @PostMapping("/add-quantity")
    public String updateQuantityCart(@RequestParam Long productId, @RequestParam String action, HttpSession session, RedirectAttributes redirectAttributes) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null) {
            for (CartItem item : cart) {
                if (Objects.equals(item.getProductId(), productId)) {
                    int currentQuantity = item.getQuantity();

                    if ("increase".equals(action)) {
                        // Check if the new quantity is in stock
                        if (productService.checkUnitInStock(currentQuantity + 1, productId)) {
                            item.setQuantity(currentQuantity + 1);
                        } else {
                            redirectAttributes.addFlashAttribute("error", "Không đủ hàng trong kho."); // Add error to RedirectAttributes
                        }
                    } else if ("decrease".equals(action) && currentQuantity > 1) {
                        item.setQuantity(currentQuantity - 1);
                    }
                    break; // Exit the loop once the product is found and updated
                }
            }
            session.setAttribute("cart", cart); // Update session cart after changes

            System.out.println("Updated quantity for productId " + productId + ": " + action);
        } else {
            System.out.println("Cart is null");
        }
        return "redirect:/cart";
    }

    @PostMapping("/add-quantity1")
    public ResponseEntity<String> updateQuantityCart(@RequestParam Long productId, @RequestParam int quantity, HttpSession session) {
        System.out.println("add-success");
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null) {
            for (CartItem item : cart) {
                if (Objects.equals(item.getProductId(), productId)) {
                    item.setQuantity(quantity);
                    break; // Exit the loop once the product is found and updated
                }
            }
            session.setAttribute("cart", cart); // Update the session cart after the change

            System.out.println("Updated quantity for productId " + productId + ": " + quantity);
            return ResponseEntity.ok("Quantity updated successfully");
        } else {
            System.out.println("Cart is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cart is null");
        }
    }

    @PostMapping("/remove-item")
    public String remove(@RequestParam Long productId, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null) {
            for (CartItem item : cart) {
                if (Objects.equals(item.getProductId(), productId)) {
                    cart.remove(item);
                    break;
                }
            }
            session.setAttribute("cart", cart); // Cập nhật lại session cart sau khi thay đổi

            System.out.println("Updated quantity for productId " + productId);
        } else {
            System.out.println("Cart is null");
        }
        return "redirect:/cart";
    }


}
