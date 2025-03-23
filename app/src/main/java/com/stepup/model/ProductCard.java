package com.stepup.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class ProductCard implements Parcelable {
    private long id;
    private String name;

    List<ProductImage> productImages;

    private Double rating;

    private Double price;
    private Double promotionPrice;
    public ProductCard(String name, List<ProductImage> productImages, Double rating) {
        this.name = name;
        this.productImages = productImages;
        this.rating = rating;
    }

    protected ProductCard(Parcel in) {
        id = in.readLong();
        name = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readDouble();
        }
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
        if (in.readByte() == 0) {
            promotionPrice = null;
        } else {
            promotionPrice = in.readDouble();
        }
    }

    public static final Creator<ProductCard> CREATOR = new Creator<ProductCard>() {
        @Override
        public ProductCard createFromParcel(Parcel in) {
            return new ProductCard(in);
        }

        @Override
        public ProductCard[] newArray(int size) {
            return new ProductCard[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductImage> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ProductImage> productImages) {
        this.productImages = productImages;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(Double promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        if (rating == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(rating);
        }
        if (price == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(price);
        }
        if (promotionPrice == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(promotionPrice);
        }
    }
}
