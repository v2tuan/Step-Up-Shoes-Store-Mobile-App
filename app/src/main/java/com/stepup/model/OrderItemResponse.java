package com.stepup.model;

import java.time.LocalDateTime;

public class OrderItemResponse {
    private long id;
    private String title;
    private ProductVariant productVariant;
    private Double price;
    private Double promotionPrice;
    private int count;
    private String createdAt;
    private String updatedAt;

    public OrderItemResponse(long id, String title, ProductVariant productVariant, Double price, Double promotionPrice, int count, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.productVariant = productVariant;
        this.price = price;
        this.promotionPrice = promotionPrice;
        this.count = count;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ProductVariant getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(ProductVariant productVariant) {
        this.productVariant = productVariant;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
