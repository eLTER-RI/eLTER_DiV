package com.ecosense.dto;

import java.io.Serializable;

public class PolygonDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String geoJSON; 
	private BoundingBoxDTO boundingBox;
	private Boolean isPolygon;
	
	public PolygonDTO() {	}
	
	public PolygonDTO(String geoJSON, BoundingBoxDTO boundingBox, Boolean isPoygon) {
		super();
		this.geoJSON = geoJSON;
		this.boundingBox = boundingBox;
		this.isPolygon = isPoygon;
	}

	public String getGeoJSON() {
		return geoJSON;
	}
	public void setGeoJSON(String geoJSON) {
		this.geoJSON = geoJSON;
	}

	public BoundingBoxDTO getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBoxDTO boundingBox) {
		this.boundingBox = boundingBox;
	}

	public Boolean getIsPolygon() {
		return isPolygon;
	}

	public void setIsPolygon(Boolean isPolygon) {
		this.isPolygon = isPolygon;
	}
	
	
	

}
