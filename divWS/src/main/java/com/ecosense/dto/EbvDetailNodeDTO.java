package com.ecosense.dto;

import java.io.Serializable;

public class EbvDetailNodeDTO  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String ebvClass;
	private String ebvName;
	 
	public EbvDetailNodeDTO(String ebvClass, String ebvName) {
		this.ebvClass = ebvClass;
		this.ebvName = ebvName;
	}

	public String getEbvClass() {
		return ebvClass;
	}

	public String getEbvName() {
		return ebvName;
	}

	public void setEbvClass(String ebvClass) {
		this.ebvClass = ebvClass;
	}

	public void setEbvName(String ebvName) {
		this.ebvName = ebvName;
	}
	 
	 

}
