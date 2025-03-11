package com.ecosense.dto.output;

import java.io.Serializable;

public class ResponsibleOrganizationODTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private String organizationName;
    private String organizationEmail;

    public ResponsibleOrganizationODTO() { }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationEmail() {
        return organizationEmail;
    }

    public void setOrganizationEmail(String organizationEmail) {
        this.organizationEmail = organizationEmail;
    }

}
