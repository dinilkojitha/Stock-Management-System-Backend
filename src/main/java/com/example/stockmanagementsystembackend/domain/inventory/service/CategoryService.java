package com.example.stockmanagementsystembackend.domain.inventory.service;

import com.example.stockmanagementsystembackend.domain.inventory.entity.Category;
import com.example.stockmanagementsystembackend.domain.inventory.repository.CategoryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
// Polymorphism: this concrete service can be used through CrudService<Category, Integer>.
public class CategoryService implements CrudService<Category, Integer> {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @Override
    public Category create(Category category) {
        return save(category);
    }

    @Override
    @Transactional
    public Category save(Category category) {
        normalizeCategory(category);
        validateCategory(category);
        category.setCategoryId(null);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> categoryNotFound(id));
    }

    @Override
    @Transactional
    public Category update(Integer id, Category category) {
        normalizeCategory(category);
        validateCategory(category);

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> categoryNotFound(id));
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());

        return categoryRepository.save(existingCategory);
    }

    @Transactional(readOnly = true)
    public List<Category> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAll();
        }
        return categoryRepository.findByNameContainingIgnoreCaseOrderByNameAsc(keyword.trim());
    }

    @Override
    @Transactional
    public void delete(Integer id) {
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
        if (category == null || category.getName() == null || category.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name must not be blank");
        }

        if (category.getName().length() > 45) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "name must not exceed 45 characters"
            );
        }
    }

    private void normalizeCategory(Category category) {
        if (category == null) {
            return;
        }
        if (category.getName() != null) {
            category.setName(category.getName().trim());
        }
        if (category.getDescription() != null) {
            category.setDescription(category.getDescription().trim());
        }
    }

    private ResponseStatusException categoryNotFound(Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Category with ID " + id + " was not found");
    }
}
