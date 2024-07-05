package com.ufm.retailsystems.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    @Column(nullable = false)
    private String productName;
    @Column(nullable = false)
    private Double unitPrice;
    @Column(nullable = false)
    private Integer unitsInStock;
    @Column(nullable = false)
    private Boolean discontinued;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(length = 600)
    private String description;
    @OneToMany(mappedBy = "product")
    private Set<OrderDetail> orderDetailSet;

    @ManyToMany(mappedBy = "products")
    private Set<Inventory> inventories;

    @ManyToOne
    private Discount discount;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "product_product_images",  // Tên của bảng liên kết
            joinColumns = @JoinColumn(name = "product_id"),  // Tên cột của bảng Product
            inverseJoinColumns = @JoinColumn(name = "image_id"),  // Tên cột của bảng ProductImages
            uniqueConstraints = @UniqueConstraint(columnNames = { "product_id", "image_id" })  // Ràng buộc duy nhất
    )
    private Set<ProductImages> productImages;

    public String getSingleImgUrl() {
        if (productImages != null && !productImages.isEmpty()) {
            return productImages.iterator().next().getImageUrl(); // get the first image URL
        }
        return null;
    }

    public String getAllImgUrls() {
        if (productImages != null && !productImages.isEmpty()) {
            return productImages.stream()
                    .map(ProductImages::getImageUrl)
                    .collect(Collectors.joining(", "));
        }
        return null; // or return a default image URL
    }
}
