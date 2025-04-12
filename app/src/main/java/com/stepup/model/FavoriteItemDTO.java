package com.stepup.model;

public class FavoriteItemDTO {
    private long productVariantId;

    public FavoriteItemDTO() {
    }

    public FavoriteItemDTO(long productVariantId) {
        this.productVariantId = productVariantId;
    }

    public long getProductVariantId() {
        return productVariantId;
    }

    public void setProductVariantId(long productVariantId) {
        this.productVariantId = productVariantId;
    }
}
