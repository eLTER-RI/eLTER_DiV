package com.ecosense.dto.output;

import java.io.Serializable;

public class PropertyRightODTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String url;

    public PropertyRightODTO() { }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

