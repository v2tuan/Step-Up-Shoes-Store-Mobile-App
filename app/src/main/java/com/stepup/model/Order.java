package com.stepup.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private long id;
    private String orderCode;
    private User user;
    private Address address;
    private Coupon coupon;
    private double subTotal;
    private Double totalPrice; // = subTotal + shippingPrice - discountPrice
    private Double discountPrice;
    private double shippingPrice = 0;
    private PaymentMethod paymentMethod;
    private List<OrderItem> orderItems;
    private PaymentStatus paymentStatus;
    private OrderShippingStatus status;
    private String createdAt;
    private String updatedAt;

    public Order(long id, String orderCode, User user, Address address, Coupon coupon, double subTotal, Double totalPrice, Double discountPrice, double shippingPrice, PaymentMethod paymentMethod, List<OrderItem> orderItems, PaymentStatus paymentStatus, OrderShippingStatus status, String createdAt, String updatedAt) {
        this.id = id;
        this.orderCode = orderCode;
        this.user = user;
        this.address = address;
        this.coupon = coupon;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
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

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
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
}
