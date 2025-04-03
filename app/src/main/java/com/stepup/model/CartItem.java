package com.stepup.model;

public class CartItem {
    private long id;
    private String title;
    private ProductVariant productVariant;
    private int count;

    public CartItem(long id, ProductVariant productVariant, int count) {
        this.id = id;
        this.productVariant = productVariant;
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProductVariant getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(ProductVariant productVariant) {
        this.productVariant = productVariant;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
