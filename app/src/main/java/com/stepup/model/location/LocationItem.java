package com.stepup.model.location;

public class LocationItem {
    private String name;
    private String code;

    public LocationItem(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
