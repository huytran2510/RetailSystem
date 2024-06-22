package com.ufm.retailsystems.services;

import com.ufm.retailsystems.entities.Product;
import com.ufm.retailsystems.repositories.ProductRepository;
import com.ufm.retailsystems.services.templates.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAllWithImages();
    }

    @Override
    public List<Product> findAll() {return productRepository.findAll();}
    @Override
    public Optional<Product> findById(Long id) { return productRepository.findById(id);}

    @Override
    public List<Product> getProductByCategoryId(Long categoryId) {return productRepository.findAllByCategoryId(categoryId);}
}
