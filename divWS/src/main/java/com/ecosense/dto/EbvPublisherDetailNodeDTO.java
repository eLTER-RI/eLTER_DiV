package com.ecosense.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EbvPublisherDetailNodeDTO  implements Serializable {
	private static final long serialVersionUID = 1L;
	
    private String publisherName;
    private String publisherEmail;
    private String publisherInstitution;
    private String publisherCountry;
	   
    public EbvPublisherDetailNodeDTO() {
		// TODO Auto-generated constructor stub
	}
	   
	public EbvPublisherDetailNodeDTO(String publisherName, String publisherEmail, String publisherInstitution,
			String publisherCountry) {
		super();
		this.publisherName = publisherName;
		this.publisherEmail = publisherEmail;
		this.publisherInstitution = publisherInstitution;
		this.publisherCountry = publisherCountry;
	}
	public String getPublisherName() {
		return publisherName;
	}
	public String getPublisherEmail() {
		return publisherEmail;
	}
	public String getPublisherInstitution() {
		return publisherInstitution;
	}
	public String getPublisherCountry() {
		return publisherCountry;
	}
	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}
	public void setPublisherEmail(String publisherEmail) {
		this.publisherEmail = publisherEmail;
	}
	public void setPublisherInstitution(String publisherInstitution) {
		this.publisherInstitution = publisherInstitution;
	}
	public void setPublisherCountry(String publisherCountry) {
		this.publisherCountry = publisherCountry;
	}
	   
	   

}
