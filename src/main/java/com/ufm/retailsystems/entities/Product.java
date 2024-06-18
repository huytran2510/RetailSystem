package com.ufm.retailsystems.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productName;
    private Double unitPrice;
    private Integer unitsInStock;
    private Boolean discontinued;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


    @OneToMany(mappedBy = "product")
    private Set<OrderDetail> orderDetailSet;

    @ManyToMany(mappedBy = "products")
    private Set<Inventory> inventories;

    @ManyToOne
    private Discount discount;

    @ManyToMany(mappedBy = "product")
    private Set<ProductImages> productImages;

    @ManyToOne
    @JoinColumn(name = "product_description_id")
    private ProductDescription productDescription ;

    public String getSingleImgUrl() {
        if (productImages != null && !productImages.isEmpty()) {
            return productImages.iterator().next().getImageUrl(); // get the first image URL
        }
        return null; // or return a default image URL
    }

    // Alternatively, you can get all image URLs concatenated into a single string
    public String getAllImgUrls() {
        if (productImages != null && !productImages.isEmpty()) {
            return productImages.stream()
                    .map(ProductImages::getImageUrl)
                    .collect(Collectors.joining(", "));
        }
        return null; // or return a default image URL
    }
}
