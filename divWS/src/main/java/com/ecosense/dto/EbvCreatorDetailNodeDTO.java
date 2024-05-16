package com.ecosense.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EbvCreatorDetailNodeDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String creatorName;
	private String creatorEmail;
	private String creatorInstitution;
	private String creatorCountry;
	
	public EbvCreatorDetailNodeDTO() {
		
	}
	
	

	public EbvCreatorDetailNodeDTO(String creatorName, String creatorEmail, String creatorInstitution, String creatorCountry) {
		super();
		this.creatorName = creatorName;
		this.creatorEmail = creatorEmail;
		this.creatorInstitution = creatorInstitution;
		this.creatorCountry = creatorCountry;
	}



	public String getCreatorName() {
		return creatorName;
	}

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public String getCreatorInstitution() {
		return creatorInstitution;
	}

	public String getCreatorCountry() {
		return creatorCountry;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}

	public void setCreatorInstitution(String creatorInstitution) {
		this.creatorInstitution = creatorInstitution;
	}

	public void setCreatorCountry(String creatorCountry) {
		this.creatorCountry = creatorCountry;
	}
	
	
	    
}
