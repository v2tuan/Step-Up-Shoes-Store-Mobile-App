package com.stepup.model;

import com.google.gson.annotations.SerializedName;

public class UserDTO {
    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")

    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("retype_password")
    private String retype_password;

    public UserDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
