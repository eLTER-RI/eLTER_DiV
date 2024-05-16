package com.ecosense.dto.output;

import java.io.Serializable;

public class SiteDetailsGeographicODTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Double elevationAvg;
	private Double elevationMax;
	private Double elevationMin;
	private String elevationUnit;
	
	private Double sizeValue;
	private String sizeUnit;
	
	public SiteDetailsGeographicODTO() { }

	public Double getElevationAvg() {
		return elevationAvg;
	}

	public void setElevationAvg(Double elevationAvg) {
		this.elevationAvg = elevationAvg;
	}

	public Double getElevationMax() {
		return elevationMax;
	}

	public void setElevationMax(Double elevationMax) {
		this.elevationMax = elevationMax;
	}

	public Double getElevationMin() {
		return elevationMin;
	}

	public void setElevationMin(Double elevationMin) {
		this.elevationMin = elevationMin;
	}

	public String getElevationUnit() {
		return elevationUnit;
	}

	public void setElevationUnit(String elevationUnit) {
		this.elevationUnit = elevationUnit;
	}

	public Double getSizeValue() {
		return sizeValue;
	}

	public void setSizeValue(Double sizeValue) {
		this.sizeValue = sizeValue;
	}

	public String getSizeUnit() {
		return sizeUnit;
	}

	public void setSizeUnit(String sizeUnit) {
		this.sizeUnit = sizeUnit;
	}
	
	
}
