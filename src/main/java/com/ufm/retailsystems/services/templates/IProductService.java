package com.ufm.retailsystems.services.templates;

import com.ufm.retailsystems.entities.Product;
import com.ufm.retailsystems.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IProductService {
    List<Product> getAllProducts();
}
