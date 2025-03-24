package com.stepup.model;

import java.util.List;

public class Color {
    private Long id;
    private String name;
    private List<ColorImage> colorImages;

    public Color(Long id, String name, List<ColorImage> colorImages) {
        this.id = id;
        this.name = name;
        this.colorImages = colorImages;
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
}
