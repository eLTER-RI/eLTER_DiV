package com.ecosense.dto;

import java.io.Serializable;

public class StandardObservationDTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private String standardObservationType;
    private String name;

    public StandardObservationDTO() {	}
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStandardObservationType() {
        return standardObservationType;
    }

    public void setStandardObservationType(String standardObservationType) {
        this.standardObservationType = standardObservationType;
    }

}
