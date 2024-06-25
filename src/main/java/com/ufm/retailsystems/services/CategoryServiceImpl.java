package com.ufm.retailsystems.services;

import com.ufm.retailsystems.entities.Category;
import com.ufm.retailsystems.repositories.CategoryRepository;
import com.ufm.retailsystems.services.templates.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
