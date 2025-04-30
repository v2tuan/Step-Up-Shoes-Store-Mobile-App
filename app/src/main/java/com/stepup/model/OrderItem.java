package com.stepup.model;

public class OrderItem {
    private long id;
    private Order order;
    private String title;
    private ProductVariant productVariant;
    private Double price;
    private Double promotionPrice;
    private int count;
//    private Delivery delivery;
    private String createdAt;
    private String updatedAt;
    private String  receiveDate;

    public OrderItem(long id, Order order, ProductVariant productVariant, Double price, Double promotionPrice, int count, String createdAt, String updatedAt, String receiveDate) {
        this.id = id;
        this.order = order;
        this.productVariant = productVariant;
        this.price = price;
        this.promotionPrice = promotionPrice;
        this.count = count;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.receiveDate = receiveDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }
}
