package com.ecosense.dto.output;

import java.io.Serializable;

public class AdditionalMetadataODTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private String name;
    private String value;

    public AdditionalMetadataODTO() { }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
