package com.stepup.model;

public class VerifyOtpRequest {
    private String email;
    private String verificationCode;

    public VerifyOtpRequest(String email, String verificationCode) {
        this.email = email;
        this.verificationCode = verificationCode;
    }

    public String getEmail() { return email; }
    public String getVerificationCode() { return verificationCode; }

}
