package com.stepup.model;

import com.google.gson.annotations.SerializedName;

public class Banner {
    @SerializedName("title")

    private String title;
    @SerializedName("imageUrl")

    private String imageUrl;

    public Banner(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public Banner(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
