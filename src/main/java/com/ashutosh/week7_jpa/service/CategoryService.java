package com.ashutosh.week7_jpa.service;

import com.ashutosh.week7_jpa.entity.Category;
import com.ashutosh.week7_jpa.exception.ResourceNotFoundException;
import com.ashutosh.week7_jpa.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIsNull();
    }

    @Transactional(readOnly = true)
    public List<Category> getChildCategories(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id
                        ));
    }

    public Category createCategory(Category category) {

        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new IllegalArgumentException(
                    "Category already exists: " + category.getName()
            );
        }

        if (category.getParent() != null
                && category.getParent().getId() != null) {

            Category parent = getCategoryById(
                    category.getParent().getId()
            );

            if (parent.getId().equals(category.getId())) {
                throw new IllegalArgumentException(
                        "Category cannot be its own parent"
                );
            }

            category.setParent(parent);
        }

        return categoryRepository.save(category);
    }

    public Category updateCategory(
            Long id,
            Category updatedCategory
    ) {

        Category existingCategory = getCategoryById(id);

        if (!existingCategory.getName()
                .equalsIgnoreCase(updatedCategory.getName())
                && categoryRepository.existsByNameIgnoreCase(
                        updatedCategory.getName())) {

            throw new IllegalArgumentException(
                    "Category already exists: "
                            + updatedCategory.getName()
            );
        }

        if (updatedCategory.getParent() != null
                && updatedCategory.getParent().getId() != null) {

            Long parentId =
                    updatedCategory.getParent().getId();

            if (id.equals(parentId)) {
                throw new IllegalArgumentException(
                        "Category cannot be its own parent"
                );
            }

            Category parent = getCategoryById(parentId);

            if (isDescendant(parent, id)) {
                throw new IllegalArgumentException(
                        "Category cannot be assigned under its descendant"
                );
            }

            existingCategory.setParent(parent);

        } else {
            existingCategory.setParent(null);
        }

        existingCategory.setName(
                updatedCategory.getName()
        );

        existingCategory.setDescription(
                updatedCategory.getDescription()
        );

        return categoryRepository.save(existingCategory);
    }

    @Transactional(readOnly = true)
    private boolean isDescendant(
            Category category,
            Long categoryId
    ) {

        Category current = category;

        while (current != null) {

            if (current.getId().equals(categoryId)) {
                return true;
            }

            current = current.getParent();
        }

        return false;
    }

    public void deleteCategory(Long id) {

        Category category = getCategoryById(id);

        if (!category.getChildren().isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot delete category with child categories"
            );
        }

        categoryRepository.delete(category);
    }
}