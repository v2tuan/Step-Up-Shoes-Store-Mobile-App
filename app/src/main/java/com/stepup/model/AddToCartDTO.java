package com.stepup.model;

public class AddToCartDTO {
    private long productVariantId;
    private int quantity;

    public AddToCartDTO(long productVariantId, int quantity) {
        this.productVariantId = productVariantId;
        this.quantity = quantity;
    }

    public long getProductVariantId() {
        return productVariantId;
    }

    public void setProductVariantId(long productVariantId) {
        this.productVariantId = productVariantId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
