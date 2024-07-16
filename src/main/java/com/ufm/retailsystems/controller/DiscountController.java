package com.ufm.retailsystems.controller;


import com.ufm.retailsystems.dto.forcreate.CDiscount;
import com.ufm.retailsystems.entities.Discount;
import com.ufm.retailsystems.services.templates.IDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api")
public class DiscountController {
    @Autowired
    private IDiscountService discountService;

    @GetMapping("/management-discount")
    public String getDiscounts(Model model,@RequestParam(value = "discountId", required = false) Long discountId ) {
        List<Discount> discountList = discountService.findAll();
        model.addAttribute("discounts",discountList);
        Discount discount = discountService.findById(discountId);
        model.addAttribute("discount", discount);
        return "/admin/management-discount";
    }

    @GetMapping("/add-discount")
    public String getDiscount(Model model) {
        CDiscount discount = new CDiscount();
        model.addAttribute("discount",discount);
        return "/admin/add-discount";
    }

    @PostMapping("/add-discount")
    public String addDiscount(Model model, @ModelAttribute("discount") CDiscount cDiscount) {
        Discount discount = discountService.add(cDiscount);
        if( discount != null) {
            return "redirect:/management-discount";
        } else {
            model.addAttribute("error","Không thể thêm khuyến mãi");
            return "/admin/management-discount";
        }
    }

    @PostMapping("/delete-discount/{discountId}")
    public String deleteDiscount(@PathVariable("discountId") Long discountId) {
        discountService.deleteById(discountId);
        return "redirect:/management-discount";
    }

    @PostMapping("/update-discount")
    public String updateDiscount(Model model, @ModelAttribute("discount") Discount cDiscount) {
        System.out.println(cDiscount.toString());
        Discount discount = discountService.update(cDiscount);
        if( discount != null) {
            return "redirect:/management-discount";
        } else {
            model.addAttribute("error","Không thể thêm khuyến mãi");
            return "/admin/management-discount";
        }
    }
}
