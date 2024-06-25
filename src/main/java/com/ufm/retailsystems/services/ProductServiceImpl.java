package com.ufm.retailsystems.services;

import com.ufm.retailsystems.dto.forcreate.CProduct;
import com.ufm.retailsystems.entities.Product;
import com.ufm.retailsystems.repositories.CategoryRepository;
import com.ufm.retailsystems.repositories.ProductRepository;
import com.ufm.retailsystems.services.templates.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    public List<Product> getAllProducts() {
        return productRepository.findAllWithImages();
    }

    @Override
    public List<Product> findAll() {return productRepository.findAll();}
    @Override
    public Optional<Product> findById(Long id) { return productRepository.findById(id);}

    @Override
    public List<Product> getProductByCategoryId(Long categoryId) {return productRepository.findAllByCategoryId(categoryId);}

    public void save(CProduct productDTO) {
        Product product = new Product();
        product.setProductName(productDTO.getProductName());
        product.setUnitPrice(productDTO.getUnitPrice());
        product.setUnitsInStock(productDTO.getUnitsInStock());
        product.setDiscontinued(productDTO.getDiscontinued());
        product.setDescription(productDTO.getDescription());
        product.setCategory(categoryRepository.findById(productDTO.getCategoryId()).orElse(null));
        productRepository.save(product);
    }

    public List<CProduct> findAllProduct() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    private CProduct convertToDTO(Product product) {
        CProduct productDTO = new CProduct();
        productDTO.setId(product.getProductId());
        productDTO.setProductName(product.getProductName());
        productDTO.setUnitPrice(product.getUnitPrice());
        productDTO.setUnitsInStock(product.getUnitsInStock());
        productDTO.setDiscontinued(product.getDiscontinued());
        productDTO.setDescription(product.getDescription());
        productDTO.setCategoryId(product.getCategory().getId());
        return productDTO;
    }
}
