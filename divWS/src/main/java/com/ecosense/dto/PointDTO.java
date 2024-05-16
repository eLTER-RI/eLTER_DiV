package com.ecosense.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PointDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Double lat;
	private Double lng;
	private BoundingBoxDTO boundingBox;
	
	public PointDTO() {	}
	
	public PointDTO(Double lat, Double lng) {
		super();
		this.lat = lat;
		this.lng = lng;
	}
	
	public PointDTO(Double lat, Double lng, BoundingBoxDTO boundingBox) {
		super();
		this.lat = lat;
		this.lng = lng;
		this.boundingBox = boundingBox;
	}

	public Double getLat() {
		return lat;
	}
	
	public void setLat(Double lat) {
		this.lat = lat;
	}
	
	public Double getLng() {
		return lng;
	}
	
	public void setLng(Double lng) {
		this.lng = lng;
	}

	public BoundingBoxDTO getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBoxDTO boundingBox) {
		this.boundingBox = boundingBox;
	}
	
}
