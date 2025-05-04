package com.stepup.model;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private long id;
    private String orderCode;
    private Address address;
    private double subTotal;
    private Double totalPrice; // = subTotal - discountPrice
    private Double discountPrice;
    private double shippingPrice = 0;
    private PaymentMethod paymentMethod;
    private List<OrderItemResponse> orderItems;
    private PaymentStatus paymentStatus;
    private OrderShippingStatus status;
    private String createdAt;
    private String updatedAt;
    private String  receiveDate;

    public OrderResponse(long id, String orderCode, double subTotal, Double totalPrice, Double discountPrice, double shippingPrice, PaymentMethod paymentMethod, List<OrderItemResponse> orderItems, PaymentStatus paymentStatus, OrderShippingStatus status, String createdAt, String updatedAt, String receiveDate) {
        this.id = id;
        this.orderCode = orderCode;
        this.subTotal = subTotal;
        this.totalPrice = totalPrice;
        this.discountPrice = discountPrice;
        this.shippingPrice = shippingPrice;
        this.paymentMethod = paymentMethod;
        this.orderItems = orderItems;
        this.paymentStatus = paymentStatus;
        this.status = status;
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

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public double getShippingPrice() {
        return shippingPrice;
    }

    public void setShippingPrice(double shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<OrderItemResponse> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemResponse> orderItems) {
        this.orderItems = orderItems;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public OrderShippingStatus getStatus() {
        return status;
    }

    public void setStatus(OrderShippingStatus status) {
        this.status = status;
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
