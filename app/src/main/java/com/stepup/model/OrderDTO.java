package com.stepup.model;

import java.util.List;

public class OrderDTO {
    private Long addressId;
    private Long couponId;
    private PaymentMethod paymentMethod;
    private List<OrderItemDTO> orderItems;

    public OrderDTO() {
    }

    public OrderDTO(Long addressId, Long couponId, PaymentMethod paymentMethod, List<OrderItemDTO> orderItems) {
        this.addressId = addressId;
        this.couponId = couponId;
        this.paymentMethod = paymentMethod;
        this.orderItems = orderItems;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }
}
