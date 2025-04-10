package com.stepup.model;

public class CouponCondition {
    private Long id;
    private Coupon coupon;
    private Attribute attribute;
    private String value;

    public CouponCondition(Long id, Coupon coupon, Attribute attribute, String value) {
        this.id = id;
        this.coupon = coupon;
        this.attribute = attribute;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
