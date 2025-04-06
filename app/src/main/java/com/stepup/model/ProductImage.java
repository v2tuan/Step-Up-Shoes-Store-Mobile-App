package com.stepup.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ProductImage implements Parcelable {
    private Long id;
    private String imageUrl;

    public ProductImage(Long id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    protected ProductImage(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        imageUrl = in.readString();
    }

    public static final Creator<ProductImage> CREATOR = new Creator<ProductImage>() {
        @Override
        public ProductImage createFromParcel(Parcel in) {
            return new ProductImage(in);
        }

        @Override
        public ProductImage[] newArray(int size) {
            return new ProductImage[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
        parcel.writeString(imageUrl);
    }
}
