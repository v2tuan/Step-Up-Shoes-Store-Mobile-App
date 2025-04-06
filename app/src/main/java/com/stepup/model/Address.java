package com.stepup.model;

public class Address {
    private Long id;
    private String fullName;
    private String address;
    private String phone;

    public Address() {
    }

    public Address(String fullName, String phone, String address) {
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddr() {
        return address;
    }

    public void setAddr(String addr) {
        this.address = addr;
    }
}
