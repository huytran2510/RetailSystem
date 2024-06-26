package com.ufm.retailsystems.services.templates;

import com.ufm.retailsystems.entities.Category;

import java.util.List;

public interface ICategoryService {
    List<Category> findAll();
}
