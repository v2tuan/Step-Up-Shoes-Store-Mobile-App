package com.stepup.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private long id;
    private String name;
    private String slug;
    private String description;
    private boolean isActive;
    private Double price;
    private Double promotionPrice;

    private List<Color> colors = new ArrayList<>();

    private List<Size> sizes = new ArrayList<>();

    private List<ProductVariant> productVariants = new ArrayList<>();

    private Double rating;

    public Product(long id, String name, String slug, String description, boolean isActive, Double price, Double promotionPrice, List<Color> colors, List<Size> sizes, List<ProductVariant> productVariants, Double rating) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.isActive = isActive;
        this.price = price;
        this.promotionPrice = promotionPrice;
        this.colors = colors;
        this.sizes = sizes;
        this.productVariants = productVariants;
        this.rating = rating;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(Double promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    public List<Size> getSizes() {
        return sizes;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }

    public List<ProductVariant> getProductVariants() {
        return productVariants;
    }

    public void setProductVariants(List<ProductVariant> productVariants) {
        this.productVariants = productVariants;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
