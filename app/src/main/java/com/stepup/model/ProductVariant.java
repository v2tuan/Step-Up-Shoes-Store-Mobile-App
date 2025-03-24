package com.stepup.model;

import java.util.List;

public class ProductVariant {
    private Long id;
    private Product product;
    private Color color;
    private Size size;
    private int quantity;

    public ProductVariant(Long id, Product product, Color color, Size size, int quantity) {
        this.id = id;
        this.product = product;
        this.color = color;
        this.size = size;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
