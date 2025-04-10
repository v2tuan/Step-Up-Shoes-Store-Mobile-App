package com.stepup.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Product implements Parcelable {
    private long id;
    private String name;
    private String slug;
    private String description;
    private boolean isActive;
    private Double price;
    private Double promotionPrice;

    private List<Color> colors = new ArrayList<>();

    private List<Size> sizes = new ArrayList<>();

    private List<ProductVariant> productVariants = new ArrayList<>();

    private Double rating;

    public Product(long id, String name, String slug, String description, boolean isActive, Double price, Double promotionPrice, List<Color> colors, List<Size> sizes, List<ProductVariant> productVariants, Double rating) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.isActive = isActive;
        this.price = price;
        this.promotionPrice = promotionPrice;
        this.colors = colors;
        this.sizes = sizes;
        this.productVariants = productVariants;
        this.rating = rating;
    }

    protected Product(Parcel in) {
        id = in.readLong();
        name = in.readString();
        slug = in.readString();
        description = in.readString();
        isActive = in.readByte() != 0;
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
        colors = in.createTypedArrayList(Color.CREATOR);
        sizes = in.createTypedArrayList(Size.CREATOR);
        productVariants = in.createTypedArrayList(ProductVariant.CREATOR);
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readDouble();
        }
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    public List<Size> getSizes() {
        return sizes;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }

    public List<ProductVariant> getProductVariants() {
        return productVariants;
    }

    public void setProductVariants(List<ProductVariant> productVariants) {
        this.productVariants = productVariants;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(slug);
        parcel.writeString(description);
        parcel.writeByte((byte) (isActive ? 1 : 0));
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
        parcel.writeTypedList(colors);
        parcel.writeTypedList(sizes);
        parcel.writeTypedList(productVariants);
        if (rating == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(rating);
        }
    }
}
