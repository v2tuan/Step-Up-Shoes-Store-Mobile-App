package com.stepup.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("fullname")
    private String fullName;

    @SerializedName("email")

    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("retype_password")
    private String retype_password;
    private String givenName;
    private String familyName;
    private String profileImage;
    private String phone;
    private int points;

    public User(String fullName, String email, String password, String retype_password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.retype_password = retype_password;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRetype_password() {
        return retype_password;
    }

    public void setRetype_password(String retype_password) {
        this.retype_password = retype_password;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
