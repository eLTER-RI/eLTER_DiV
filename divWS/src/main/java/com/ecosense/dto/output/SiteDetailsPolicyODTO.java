package com.ecosense.dto.output;

import java.io.Serializable;

public class SiteDetailsPolicyODTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String url;
	private String notes;
	private String rights;
	
	public SiteDetailsPolicyODTO() {
		// TODO Auto-generated constructor stub
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}
	
	

}
