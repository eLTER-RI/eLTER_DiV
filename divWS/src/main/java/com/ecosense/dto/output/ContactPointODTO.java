package com.ecosense.dto.output;

import java.io.Serializable;

public class ContactPointODTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private String contactName;
    private String contactEmail;

    public ContactPointODTO() { }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

}
