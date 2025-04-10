package com.stepup.model;

import java.util.List;

public class Coupon {
    private Long id;
    private String code;
    private boolean active;
    private List<CouponCondition> couponConditionList;

    public Coupon(Long id, String code, boolean active, List<CouponCondition> couponConditionList) {
        this.id = id;
        this.code = code;
        this.active = active;
        this.couponConditionList = couponConditionList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<CouponCondition> getCouponConditionList() {
        return couponConditionList;
    }

    public void setCouponConditionList(List<CouponCondition> couponConditionList) {
        this.couponConditionList = couponConditionList;
    }
}
