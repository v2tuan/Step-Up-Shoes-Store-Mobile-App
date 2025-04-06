package com.stepup.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class Color implements Parcelable {
    private Long id;
    private String name;
    private List<ColorImage> colorImages;

    public Color(Long id, String name, List<ColorImage> colorImages) {
        this.id = id;
        this.name = name;
        this.colorImages = colorImages;
    }

    protected Color(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();

        ///////////////////////
        colorImages = in.createTypedArrayList(ColorImage.CREATOR); // ✅ Đọc lại
    }

    public static final Creator<Color> CREATOR = new Creator<Color>() {
        @Override
        public Color createFromParcel(Parcel in) {
            return new Color(in);
        }

        @Override
        public Color[] newArray(int size) {
            return new Color[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Color color = (Color) obj;
        return Objects.equals(id, color.id) && Objects.equals(name, color.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ColorImage> getColorImages() {
        return colorImages;
    }

    public void setColorImages(List<ColorImage> colorImages) {
        this.colorImages = colorImages;
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
        parcel.writeString(name);

        //////////////////////////
        parcel.writeTypedList(colorImages); // ✅ Thay vì writeParcelable
    }
}
