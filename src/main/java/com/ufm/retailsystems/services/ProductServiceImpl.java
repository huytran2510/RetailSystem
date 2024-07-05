package com.ufm.retailsystems.services;

import com.ufm.retailsystems.dto.forcreate.CProduct;
import com.ufm.retailsystems.entities.Category;
import com.ufm.retailsystems.entities.Discount;
import com.ufm.retailsystems.entities.Product;
import com.ufm.retailsystems.entities.ProductImages;
import com.ufm.retailsystems.repositories.CategoryRepository;
import com.ufm.retailsystems.repositories.DiscountRepository;
import com.ufm.retailsystems.repositories.ProductImageRepository;
import com.ufm.retailsystems.repositories.ProductRepository;
import com.ufm.retailsystems.services.templates.IProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductServiceImpl implements IProductService {

    private final EntityManager entityManager;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private FileServiceImpl fileService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductImageRepository productImageRepository;

    public ProductServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAllWithImages();
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getProductByCategoryId(Long categoryId) {
        return productRepository.findAllByCategoryId(categoryId);
    }

    @Transactional
    public void save(CProduct productDTO, MultipartFile file) throws IOException {
        // Fetch or create a product
        Product product;
        if (productDTO.getId() != null) {
            product = productRepository.findById(productDTO.getId()).orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productDTO.getId()));
        } else {
            product = new Product();
        }
        // Set product details
        product.setProductName(productDTO.getProductName());
        product.setUnitPrice(productDTO.getUnitPrice());
        product.setUnitsInStock(productDTO.getUnitsInStock());
        product.setDiscontinued(true);
        product.setDescription(productDTO.getDescription());
        product.setCategory(categoryRepository.findById(productDTO.getCategoryId()).orElse(null));
        product.setDiscount(discountRepository.findById(productDTO.getDiscountId()).orElse(null));
        // Save or update the product
        product = productRepository.save(product);
        // Upload file and create ProductImages instance
        fileService.uploadFile(file);
        String imageUrl = file.getOriginalFilename();
                ProductImages productImage = new ProductImages();
        productImage.setImageUrl("/img/" + imageUrl);
        productImage.getProducts().add(product);
        // Save the ProductImages
        productImageRepository.save(productImage);
        if (product.getProductImages() == null) {
            product.setProductImages(new HashSet<>()); // Ensure productImages set is initialized
        }
        product.getProductImages().add(productImage);
        // Save the product again to establish the relationship
        productRepository.save(product);
    }


    public void update(CProduct productDTO) {
        Product product = new Product();
        product.setProductId(productDTO.getId());
        product.setProductName(productDTO.getProductName());
        product.setUnitPrice(productDTO.getUnitPrice());
        product.setUnitsInStock(productDTO.getUnitsInStock());
        product.setDiscontinued(productDTO.getDiscontinued());
        product.setDescription(productDTO.getDescription());
        product.setCategory(categoryRepository.findById(productDTO.getCategoryId()).orElse(null));
        product.setDiscount(discountRepository.findById(productDTO.getDiscountId()).orElse(null));
        productRepository.save(product);
    }

    public CProduct findProductById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            return convertToDTO(productOptional.get());
        }
        return null;
    }

    private CProduct convertToDTO(Product product) {
        CProduct productDTO = new CProduct();
        productDTO.setId(product.getProductId());
        productDTO.setProductName(product.getProductName());
        productDTO.setUnitPrice(product.getUnitPrice());
        productDTO.setUnitsInStock(product.getUnitsInStock());
        productDTO.setDiscontinued(product.getDiscontinued());
        productDTO.setDescription(product.getDescription());
        productDTO.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        return productDTO;
    }

    public void delete(Long productId) {
        productRepository.deleteById(productId);
    }

}
