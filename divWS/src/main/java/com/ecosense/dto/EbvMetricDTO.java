package com.ecosense.dto;

import java.io.Serializable;

public class EbvMetricDTO  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	private String unit;
	
	public EbvMetricDTO() {
		// TODO Auto-generated constructor stub
	}

	public EbvMetricDTO(String name, String description, String unit) {
		super();
		this.name = name;
		this.description = description;
		this.unit = unit;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getUnit() {
		return unit;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	

}
