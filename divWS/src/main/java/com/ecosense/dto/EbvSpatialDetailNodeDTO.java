package com.ecosense.dto;

import java.io.Serializable;

public class EbvSpatialDetailNodeDTO  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String ebvSpatialScope;
	private String ebvSpatialDescription;
	
	public EbvSpatialDetailNodeDTO(String ebvSpatialScope, String ebvSpatialDescription) {
		super();
		this.ebvSpatialScope = ebvSpatialScope;
		this.ebvSpatialDescription = ebvSpatialDescription;
	}

	public String getEbvSpatialScope() {
		return ebvSpatialScope;
	}

	public String getEbvSpatialDescription() {
		return ebvSpatialDescription;
	}

	public void setEbvSpatialScope(String ebvSpatialScope) {
		this.ebvSpatialScope = ebvSpatialScope;
	}

	public void setEbvSpatialDescription(String ebvSpatialDescription) {
		this.ebvSpatialDescription = ebvSpatialDescription;
	}
	
	
	
	

}
