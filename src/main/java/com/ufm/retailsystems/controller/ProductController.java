package com.ufm.retailsystems.controller;

import com.ufm.retailsystems.dto.slider.Slide;
import com.ufm.retailsystems.entities.Product;
import com.ufm.retailsystems.services.templates.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ProductController {
    @Autowired
    private HttpSession httpSession;

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
        List<String> formatPriceDiscount = products.stream()
                .map(product -> formatPriceToVND(product.getUnitPrice()-product.getUnitPrice()*product.getDiscount().getDiscountPercent()))
                .collect(Collectors.toList());
        model.addAttribute("formatPriceDiscount", formatPriceDiscount);
        List<Slide> slides = new ArrayList<>();
        slides.add(new Slide("/img/slide1.png", "Title 1", "Description 1"));
        slides.add(new Slide("/img/slide2.png", "Title 2", "Description 2"));
        slides.add(new Slide("/img/slide3.png", "Title 3", "Description 3"));
        model.addAttribute("slides", slides);
        return "/product/list-product";
    }

    public String formatPriceToVND(double price) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        DecimalFormat decimalFormat = (DecimalFormat) formatter;
        decimalFormat.applyPattern("#,###");
        return decimalFormat.format(price) + "đ";
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

    @GetMapping("/products/{productId}")
    public String getProductDetail(@PathVariable Long productId, Model model) {
        Optional<Product> optionalProduct = iProductService.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            model.addAttribute("product", product);
            String formattedPrices = formatPriceToVND(product.getUnitPrice()) ;
            model.addAttribute("formattedPrices", formattedPrices);
            return "/product/product-detail";
        } else {
            // Xử lý khi không tìm thấy sản phẩm
            return "error"; // Trang lỗi hoặc trang thông báo không tìm thấy sản phẩm
        }
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

    @GetMapping("/payment")
    public String payment(Model model) {
        return "order-page";
    }
}
