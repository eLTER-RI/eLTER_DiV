package com.ecosense.dto.output;

import java.io.Serializable;

public class HabitatReferenceODTO implements Serializable {
	private static final long serialVersionUID = 1L;
    
    private String soHabitatURI;
    private String soHabitatCode;

    public HabitatReferenceODTO() { }

    public String getSoHabitatURI() {
        return soHabitatURI;
    }

    public void setSoHabitatURI(String soHabitatURI) {
        this.soHabitatURI = soHabitatURI;
    }

    public String getSoHabitatCode() {
        return soHabitatCode;
    }

    public void setSoHabitatCode(String soHabitatCode) {
        this.soHabitatCode = soHabitatCode;
    }

}
