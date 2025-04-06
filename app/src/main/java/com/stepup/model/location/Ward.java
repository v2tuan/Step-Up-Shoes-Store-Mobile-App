package com.stepup.model.location;

public class Ward {
    private String _id;
    private String name;
    private String type;
    private String slug;
    private String name_with_type;
    private String path;
    private String path_with_type;
    private String code;
    private String parent_code;
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
