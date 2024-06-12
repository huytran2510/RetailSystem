package com.ufm.retailsystems.controller;

import com.ufm.retailsystems.dto.cart.CartItem;
import com.ufm.retailsystems.entities.Product;
import com.ufm.retailsystems.services.templates.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class CartController {
    @Autowired
    private IProductService productService;

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("productId") Long productId, HttpSession session, RedirectAttributes redirectAttributes) {
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
                cart.add(cartItem);
            }
            return "redirect:/cart";
        } else {
            redirectAttributes.addFlashAttribute("error", "Product not found");
            return "redirect:/products";
        }
    }

    @GetMapping("cart")
    public String cart(Model model, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        model.addAttribute("cart", cart);
        return "cart";
    }
}