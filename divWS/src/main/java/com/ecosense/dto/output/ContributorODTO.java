package com.ecosense.dto.output;

import java.io.Serializable;

public class ContributorODTO implements Serializable {
	private static final long serialVersionUID = 1L;
    
    private String email;
    private String familyName;
    private String fullName;
    private String givenName;
    private String type;

    public ContributorODTO() { }

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
