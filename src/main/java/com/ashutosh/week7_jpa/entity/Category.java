package com.ashutosh.week7_jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "categories",
        indexes = {
                @Index(name = "idx_category_name", columnList = "name"),
                @Index(name = "idx_category_parent", columnList = "parent_category_id")
        }
)
public class Category extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category name is required")
    @Size(
            min = 2,
            max = 100,
            message = "Category name must be between 2 and 100 characters"
    )
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Size(
            max = 500,
            message = "Description cannot exceed 500 characters"
    )
    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    @JsonIgnoreProperties({
            "products",
            "children",
            "parent"
    })
    private Category parent;

    @OneToMany(
            mappedBy = "parent",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JsonIgnore
    private List<Category> children = new ArrayList<>();

    @OneToMany(
            mappedBy = "category",
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<Product> products = new ArrayList<>();

    public Category() {
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}