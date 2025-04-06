package com.stepup.model.location;

public class Province {
    private String _id;
    private String name;
    private String slug;
    private String type;
    private String name_with_type;
    private String code;
    private boolean isDeleted;

    public String getName() {
        return name;
    }

    public String getName_with_type() {
        return name_with_type;
    }

    public String getCode() {
        return code;
    }
}
