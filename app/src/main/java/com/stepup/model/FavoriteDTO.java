package com.stepup.model;

public class FavoriteDTO {
    private long colorId;

    private Double price;
    public FavoriteDTO(long colorId ,Double price) {
        this.colorId = colorId;
        this.price = price;
    }

    public FavoriteDTO() {
    }

    public long getColorId() {
        return colorId;
    }

    public void setColorId(long colorId) {
        this.colorId = colorId;
    }
}
