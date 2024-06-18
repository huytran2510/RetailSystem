package com.ufm.retailsystems.controller;

import com.ufm.retailsystems.dto.slider.Slide;
import com.ufm.retailsystems.entities.Product;
import com.ufm.retailsystems.services.templates.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
public class ProductController {
    @Autowired
    private HttpSession httpSession;
    //    @GetMapping("/products")
//    public String productList(Model model) {
//        List<Product> products = getProducts(); // You would typically retrieve products from a database
//        model.addAttribute("products", products);
//        return "product-list";
//    }
//
//    private List<Product> getProducts() {
//        // Dummy data for demonstration purposes
//        List<Product> products = new ArrayList<>();
//        products.add(new Product(1L, "iPhone 12", "Apple iPhone 12", 999.99));
//        products.add(new Product(2L, "Samsung Galaxy S21", "Samsung Galaxy S21", 899.99));
//        products.add(new Product(3L, "Google Pixel 5", "Google Pixel 5", 699.99));
//        return products;
//    }
    @Autowired
    private IProductService iProductService;

    @GetMapping("/products")
    public String getProducts(Model model) {
        List<Product> products = iProductService.getAllProducts();
        model.addAttribute("products", products);
        List<String> formattedPrices = products.stream()
                .map(product -> formatPriceToVND(product.getUnitPrice()))
                .collect(Collectors.toList());
        model.addAttribute("formattedPrices", formattedPrices);
        List<Slide> slides = new ArrayList<>();
        slides.add(new Slide("/img/slide1.png", "Title 1", "Description 1"));
        slides.add(new Slide("/img/slide2.png", "Title 2", "Description 2"));
        slides.add(new Slide("/img/slide3.png", "Title 3", "Description 3"));
        model.addAttribute("slides", slides);
        return "list-product";
    }

    public String formatPriceToVND(double price) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        DecimalFormat decimalFormat = (DecimalFormat) formatter;
        decimalFormat.applyPattern("#,###");
        return decimalFormat.format(price) + " ₫";
    }


    @GetMapping("/slider")
    public String getSlider(Model model) {
        List<Slide> slides = new ArrayList<>();
        slides.add(new Slide("/img/slide1.png", "Title 1", "Description 1"));
        slides.add(new Slide("/img/slide1.png", "Title 2", "Description 2"));
        slides.add(new Slide("/img/slide1.png", "Title 3", "Description 3"));
        model.addAttribute("slides", slides);
        return "/layout/nav-bar-banned";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/management-product")
    public String managementProduct(Model model) {
        List<Product> products = iProductService.findAll();
        List<String> formattedPrices = products.stream()
                .map(product -> formatPriceToVND(product.getUnitPrice()))
                .collect(Collectors.toList());
        model.addAttribute("formattedPrices", formattedPrices);
        model.addAttribute("products" , products);
        return "management-product";
    }
}
