package com.example.stockmanagementsystembackend.service;

import com.example.stockmanagementsystembackend.entity.Category;
import com.example.stockmanagementsystembackend.repository.CategoryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category createCategory(Category category) {
        validateCategory(category);
        category.setId(null);
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> categoryNotFound(id));
    }

    @Transactional
    public Category updateCategory(Integer id, Category category) {
        validateCategory(category);

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> categoryNotFound(id));
        existingCategory.setCategoryName(category.getCategoryName());
        existingCategory.setDiscription(category.getDiscription());

        return categoryRepository.save(existingCategory);
    }

    @Transactional
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> categoryNotFound(id));

        try {
            categoryRepository.delete(category);
            categoryRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Category cannot be deleted because it is in use"
            );
        }
    }

    private void validateCategory(Category category) {
        if (category == null || category.getCategoryName() == null || category.getCategoryName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "categoryName must not be blank");
        }

        if (category.getCategoryName().length() > 45) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "categoryName must not exceed 45 characters"
            );
        }
    }

    private ResponseStatusException categoryNotFound(Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Category with ID " + id + " was not found");
    }
}
