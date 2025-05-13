package com.stepup.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewResponse implements Parcelable {
    private String userFullName;
    private String productName;
    private String content;
    private Integer rating;
    private List<String> imageUrls;
    private String createdAt;

    public ReviewResponse() {
    }

    public ReviewResponse(String userFullName, String productName, String content, Integer rating, List<String> imageUrls, String  createdAt) {
        this.userFullName = userFullName;
        this.productName = productName;
        this.content = content;
        this.rating = rating;
        this.imageUrls = imageUrls;
        this.createdAt = createdAt;
    }

    protected ReviewResponse(Parcel in) {
        userFullName = in.readString();
        productName = in.readString();
        content = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readInt();
        }
        imageUrls = in.createStringArrayList();
        createdAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userFullName);
        dest.writeString(productName);
        dest.writeString(content);
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(rating);
        }
        dest.writeStringList(imageUrls);
        dest.writeString(createdAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReviewResponse> CREATOR = new Creator<ReviewResponse>() {
        @Override
        public ReviewResponse createFromParcel(Parcel in) {
            return new ReviewResponse(in);
        }

        @Override
        public ReviewResponse[] newArray(int size) {
            return new ReviewResponse[size];
        }
    };

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String  getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String  createdAt) {
        this.createdAt = createdAt;
    }
}
