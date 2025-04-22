package com.stepup.model;

public class Favorite {
    private long id;
    private String title;
    private Color color;

    private Double price;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Favorite() {
    }

    public Favorite(Color color, String title, long id, double price) {
        this.color = color;
        this.title = title;
        this.id = id;
    }

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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", color=" + color +
                ", price=" + price +
                '}';
    }
}
