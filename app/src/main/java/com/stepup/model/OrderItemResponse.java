package com.stepup.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.time.LocalDateTime;

public class OrderItemResponse implements Parcelable {
    private long id;
    private String title;
    private ProductVariant productVariant;
    private Double price;
    private Double promotionPrice;
    private int count;
    private String createdAt;
    private String updatedAt;

    public OrderItemResponse(long id, String title, ProductVariant productVariant, Double price, Double promotionPrice, int count, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.productVariant = productVariant;
        this.price = price;
        this.promotionPrice = promotionPrice;
        this.count = count;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    protected OrderItemResponse(Parcel in) {
        id = in.readLong();
        title = in.readString();
        productVariant = in.readParcelable(ProductVariant.class.getClassLoader());
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
        count = in.readInt();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeParcelable(productVariant, flags);
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
        dest.writeInt(count);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderItemResponse> CREATOR = new Creator<OrderItemResponse>() {
        @Override
        public OrderItemResponse createFromParcel(Parcel in) {
            return new OrderItemResponse(in);
        }

        @Override
        public OrderItemResponse[] newArray(int size) {
            return new OrderItemResponse[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ProductVariant getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(ProductVariant productVariant) {
        this.productVariant = productVariant;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
