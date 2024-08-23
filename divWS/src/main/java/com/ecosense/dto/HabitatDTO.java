package com.ecosense.dto;

import java.io.Serializable;

public class HabitatDTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private String name;

    public HabitatDTO() {	}
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
