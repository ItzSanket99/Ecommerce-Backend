package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServicesImpl implements CategoryService {
    private List<Category> categories = new ArrayList<>();
    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public void CreateCategories(Category category) {
        categories.add(category);
    }
}
