package com.stepup.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductCard implements Parcelable {
    private long id;
    private String name;

    @SerializedName("imageUrl")
    private String ImageUrl;

    private Double rating;

    private Double price;
    private Double promotionPrice;

    private boolean fav;

    public ProductCard(long id, String name, String imageUrl, Double rating, Double price, Double promotionPrice, boolean fav) {
        this.id = id;
        this.name = name;
        ImageUrl = imageUrl;
        this.rating = rating;
        this.price = price;
        this.promotionPrice = promotionPrice;
        this.fav =fav;
    }

    public ProductCard() {
    }

    protected ProductCard(Parcel in) {
        id = in.readLong();
        name = in.readString();
        ImageUrl = in.readString();
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(ImageUrl);
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(rating);
        }
        if (price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(price);
        }
        if (promotionPrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(promotionPrice);
        }
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
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

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }
}
