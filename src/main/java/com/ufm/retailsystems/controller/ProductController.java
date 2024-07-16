package com.ufm.retailsystems.controller;

import com.ufm.retailsystems.dto.forcreate.CProduct;
import com.ufm.retailsystems.dto.slider.Slide;
import com.ufm.retailsystems.entities.Product;
import com.ufm.retailsystems.services.templates.ICategoryService;
import com.ufm.retailsystems.services.templates.IDiscountService;
import com.ufm.retailsystems.services.templates.IFileService;
import com.ufm.retailsystems.services.templates.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
public class ProductController {
    @Autowired
    private HttpSession httpSession;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IFileService fileService;

    @Autowired
    private IDiscountService discountService;

    private static final String UPLOAD_DIR = "src/main/resources/static/img/";

    @GetMapping("/mobile")
    public String getProducts(Model model) {
        List<Product> products = iProductService.getAllProducts();
        model.addAttribute("products", products);
        List<String> formattedPrices = products.stream()
                .map(product -> formatPriceToVND(product.getUnitPrice()))
                .collect(Collectors.toList());
        model.addAttribute("formattedPrices", formattedPrices);
        List<String> formatPriceDiscount = products.stream()
                .map(product -> formatPriceToVND(product.getUnitPrice() - product.getUnitPrice() * product.getDiscount().getDiscountPercent()))
                .collect(Collectors.toList());
        model.addAttribute("formatPriceDiscount", formatPriceDiscount);
        return "/product/list-product";
    }

    @GetMapping("/mobile/{categoryId}")
    public String getProductCategory(@PathVariable Long categoryId, Model model) {
        List<Product> products = iProductService.getProductByCategoryId(categoryId);
        model.addAttribute("products", products);
        List<String> formattedPrices = products.stream()
                .map(product -> formatPriceToVND(product.getUnitPrice()))
                .collect(Collectors.toList());
        model.addAttribute("formattedPrices", formattedPrices);
        List<String> formatPriceDiscount = products.stream()
                .map(product -> formatPriceToVND(product.getUnitPrice() - product.getUnitPrice() * product.getDiscount().getDiscountPercent()))
                .collect(Collectors.toList());
        model.addAttribute("formatPriceDiscount", formatPriceDiscount);
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

    @GetMapping("/mobile/detail/{productId}")
    public String getProductDetail(@PathVariable Long productId, Model model) {
        Optional<Product> optionalProduct = iProductService.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            model.addAttribute("product", product);
            String formattedPrices = formatPriceToVND(product.getUnitPrice());
            model.addAttribute("formattedPrices", formattedPrices);
            return "/product/product-detail";
        } else {
            // Xử lý khi không tìm thấy sản phẩm
            return "error"; // Trang lỗi hoặc trang thông báo không tìm thấy sản phẩm
        }
    }

    @GetMapping("/management-product")
    public String managementProduct(Model model, @RequestParam(value = "productId", required = false) Long productId) {
        List<Product> products = iProductService.findAll();
        List<String> formattedPrices = products.stream()
                .map(product -> formatPriceToVND(product.getUnitPrice()))
                .collect(Collectors.toList());
        CProduct cProduct = new CProduct();
        if (productId != null) {
            cProduct = iProductService.findProductById(productId);
            model.addAttribute("productUpdate", cProduct);
        }
        model.addAttribute("formattedPrices", formattedPrices);
        model.addAttribute("products", products);


        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("productDTO", new CProduct());
        model.addAttribute("discounts", discountService.findAll());
        return "/admin/management-product";
    }

    @GetMapping("/payment")
    public String payment(Model model) {
        return "order-page";
    }

    @GetMapping("/product/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("productDTO", new CProduct());
        model.addAttribute("discounts", discountService.findAll());
        return "/product/add-product";
    }

    @PostMapping("/add-a")
    public String addProduct(@ModelAttribute("productDTO") CProduct productDTO,
                             @RequestParam("file") MultipartFile imageFile) {
        String fileName = imageFile.getOriginalFilename();
        System.out.println(fileName);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Save the product with the updated DTO
                iProductService.save(productDTO, imageFile);

                return "redirect:/product/add"; // Redirect về trang thêm sản phẩm thành công
            } catch (Exception e) {
                return "Failed to created: " + e.getMessage();
            }
        } else {
            return "Please select a file to upload";
        }
    }

    @PostMapping("/update-product")
    public String updateProduct(@ModelAttribute("productUpdate") CProduct productDTO) {
        try {
            // Save the product with the updated DTO
            iProductService.update(productDTO);
            return "redirect:/management-product"; // Redirect về trang thêm sản phẩm thành công
        } catch (Exception e) {
            return "Failed to update: " + e.getMessage();
        }
    }

    @PostMapping("/delete-product/{productId}")
    public String deleteProduct(@PathVariable("productId") Long productId) {
        iProductService.delete(productId);
        return "redirect:/management-product";
    }
}
