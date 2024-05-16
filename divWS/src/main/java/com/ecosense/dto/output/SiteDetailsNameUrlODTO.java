package com.ecosense.dto.output;

import java.io.Serializable;

public class SiteDetailsNameUrlODTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String url;
	private String name;
	
	public SiteDetailsNameUrlODTO() {
	
	}
	
	public SiteDetailsNameUrlODTO(String url, String name) {
		super();
		this.url = url;
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

}
