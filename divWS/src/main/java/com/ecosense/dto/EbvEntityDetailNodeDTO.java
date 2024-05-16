package com.ecosense.dto;

import java.io.Serializable;

public class EbvEntityDetailNodeDTO  implements Serializable {
	private static final long serialVersionUID = 1L;
	
    private String ebvEntityType;
    private String ebvEntityScope;
    private String ebvEntityClassificationName;
    private String ebvEntityClassificationUrl;
    
    public EbvEntityDetailNodeDTO() {
		// TODO Auto-generated constructor stub
	}

	public EbvEntityDetailNodeDTO(String ebvEntityType, String ebvEntityScope, String ebvEntityClassificationName,
			String ebvEntityClassificationUrl) {
		super();
		this.ebvEntityType = ebvEntityType;
		this.ebvEntityScope = ebvEntityScope;
		this.ebvEntityClassificationName = ebvEntityClassificationName;
		this.ebvEntityClassificationUrl = ebvEntityClassificationUrl;
	}

	public String getEbvEntityType() {
		return ebvEntityType;
	}

	public String getEbvEntityScope() {
		return ebvEntityScope;
	}

	public String getEbvEntityClassificationName() {
		return ebvEntityClassificationName;
	}

	public String getEbvEntityClassificationUrl() {
		return ebvEntityClassificationUrl;
	}

	public void setEbvEntityType(String ebvEntityType) {
		this.ebvEntityType = ebvEntityType;
	}

	public void setEbvEntityScope(String ebvEntityScope) {
		this.ebvEntityScope = ebvEntityScope;
	}

	public void setEbvEntityClassificationName(String ebvEntityClassificationName) {
		this.ebvEntityClassificationName = ebvEntityClassificationName;
	}

	public void setEbvEntityClassificationUrl(String ebvEntityClassificationUrl) {
		this.ebvEntityClassificationUrl = ebvEntityClassificationUrl;
	}
	
	
    
    

}
