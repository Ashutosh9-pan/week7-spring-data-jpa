package com.ashutosh.week7_jpa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(
            min = 2,
            max = 100,
            message = "Category name must be between 2 and 100 characters"
    )
    private String name;

    @Size(
            max = 500,
            message = "Description cannot exceed 500 characters"
    )
    private String description;

    private Long parentId;

    public CategoryRequest() {
    }

    public CategoryRequest(
            String name,
            String description,
            Long parentId
    ) {
        this.name = name;
        this.description = description;
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}