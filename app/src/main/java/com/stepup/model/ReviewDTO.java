package com.stepup.model;

import java.util.List;

public class ReviewDTO {
    private Long productVariantId;
    private Long orderId;
    private String content;
    private Integer rating;
    private List<String> imagePaths;

    public ReviewDTO() {
    }

    public ReviewDTO(Long productId, Long orderId, String content, Integer rating, List<String> imagePaths) {
        this.productVariantId = productId;
        this.orderId = orderId;
        this.content = content;
        this.rating = rating;
        this.imagePaths = imagePaths;
    }

    public Long getProductVariantId() {
        return productVariantId;
    }

    public void setProductVariantId(Long productVariantId) {
        this.productVariantId = productVariantId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }
}
