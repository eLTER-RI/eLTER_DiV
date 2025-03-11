package com.ecosense.dto.output;

import java.io.Serializable;

public class CreatorODTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private String email;
    private String familyName;
    private String givenName;

    public CreatorODTO() { }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

}
