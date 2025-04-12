package com.stepup.model;

public class OrderItemDTO {
    private Long productVariantId;
    private Integer count;
//    private Long deliveryId;

    public OrderItemDTO() {
    }

    public OrderItemDTO(Long productVariantId, Integer count) {
        this.productVariantId = productVariantId;
        this.count = count;
    }

    public Long getProductVariantId() {
        return productVariantId;
    }

    public void setProductVariantId(Long productVariantId) {
        this.productVariantId = productVariantId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
