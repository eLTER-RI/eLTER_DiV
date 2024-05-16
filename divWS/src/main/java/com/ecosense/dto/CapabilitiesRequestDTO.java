package com.ecosense.dto;

import java.io.Serializable;

public class CapabilitiesRequestDTO  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String url;
	private String authName;
	private String authPassword;
	
	public CapabilitiesRequestDTO() {
		
	}

	public String getUrl() {
		return url;
	}

	public String getAuthName() {
		return authName;
	}

	public String getAuthPassword() {
		return authPassword;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setAuthName(String authName) {
		this.authName = authName;
	}

	public void setAuthPassword(String aythPassword) {
		this.authPassword = aythPassword;
	}
	
	

}
