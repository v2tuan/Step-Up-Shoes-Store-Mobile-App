package com.stepup.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CartItem implements Parcelable {
    private long id;
    private String title;
    private ProductVariant productVariant;
    private int count;

    public CartItem(long id, ProductVariant productVariant, int count) {
        this.id = id;
        this.productVariant = productVariant;
        this.count = count;
    }

    protected CartItem(Parcel in) {
        id = in.readLong();
        title = in.readString();
        productVariant = in.readParcelable(ProductVariant.class.getClassLoader());
        count = in.readInt();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProductVariant getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(ProductVariant productVariant) {
        this.productVariant = productVariant;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeParcelable(productVariant, i);
        parcel.writeInt(count);
    }
}
