package com.ufm.retailsystems.services.templates;

import com.ufm.retailsystems.dto.forcreate.CProduct;
import com.ufm.retailsystems.entities.Product;
import com.ufm.retailsystems.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface IProductService {
    List<Product> getAllProducts();

    Optional<Product> findById(Long id);

    List<Product> getProductByCategoryId(Long categoryId);
    List<Product> findAll();

    void save(CProduct productDTO);

    List<CProduct> findAllProduct();
}
