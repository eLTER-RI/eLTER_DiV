package com.ecosense.dto.output;

import java.io.Serializable;

public class DescriptionODTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String description;
    private String type;

    public DescriptionODTO() { }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    
    
}
