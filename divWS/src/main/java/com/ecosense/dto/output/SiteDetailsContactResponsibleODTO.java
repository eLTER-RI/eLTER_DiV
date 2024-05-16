package com.ecosense.dto.output;

import java.io.Serializable;

public class SiteDetailsContactResponsibleODTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String type;
	private String orcid;
	private String email;
	
	public SiteDetailsContactResponsibleODTO() {}
	
	public SiteDetailsContactResponsibleODTO(String name, String type, String orcid, String email) {
		super();
		this.name = name;
		this.type = type;
		this.orcid = orcid;
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOrcid() {
		return orcid;
	}
	public void setOrcid(String orcid) {
		this.orcid = orcid;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
