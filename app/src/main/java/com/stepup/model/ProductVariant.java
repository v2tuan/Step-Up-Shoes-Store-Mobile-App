package com.stepup.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class ProductVariant implements Parcelable {
    private Long id;
    private Product product;
    private Color color;
    private Size size;
    private int quantity;
    private Double price;
    private Double promotionPrice;

    public ProductVariant(Long id, Product product, Color color, Size size, int quantity) {
        this.id = id;
        this.product = product;
        this.color = color;
        this.size = size;
        this.quantity = quantity;
    }

    protected ProductVariant(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        quantity = in.readInt();
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
        // Luu y o day
        color = in.readParcelable(Color.class.getClassLoader());
        size = in.readParcelable(Size.class.getClassLoader());
    }

    public static final Creator<ProductVariant> CREATOR = new Creator<ProductVariant>() {
        @Override
        public ProductVariant createFromParcel(Parcel in) {
            return new ProductVariant(in);
        }

        @Override
        public ProductVariant[] newArray(int size) {
            return new ProductVariant[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
        parcel.writeInt(quantity);
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

        // Luu y o day
        parcel.writeParcelable(color, i);
        parcel.writeParcelable(size, i);
    }
}
