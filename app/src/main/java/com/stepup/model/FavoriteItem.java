package com.stepup.model;

public class FavoriteItem {
    private long id;
    private String title;
    private ProductVariant productVariant;

    public FavoriteItem() {
    }

    public FavoriteItem(String title, ProductVariant productVariant) {
        this.title = title;
        this.productVariant = productVariant;
    }

    public FavoriteItem(long id, String title, ProductVariant productVariant) {
        this.id = id;
        this.title = title;
        this.productVariant = productVariant;
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
}
