package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.ReadOnlyFileSystemException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServicesImpl implements CategoryService {
    private List<Category> categories = new ArrayList<>();
    private Long nextId = 1L;
    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public void CreateCategories(Category category) {
        category.setCategoryId(nextId++);
        categories.add(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = (Category) categories.stream()
                .filter(category1 -> category1.getCategoryId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource Not Found"));

        categories.remove(category);
        return "Category with categoryId: "+ categoryId +" Deleted Successfully";
    }
}
