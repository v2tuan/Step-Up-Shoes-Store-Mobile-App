package com.stepup.model;

import java.io.Serializable;

public class Address implements Serializable {
    private Long id;
    private String fullName;
    private String addr;
    private String phone;

    public Address() {
    }

    public Address(Long id, String fullName, String addr, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.addr = addr;
        this.phone = phone;
    }

    public Address(String fullName, String phone, String address) {
        this.fullName = fullName;
        this.phone = phone;
        this.addr = address;
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

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
