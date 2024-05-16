package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.Date;

public class SatelliteODTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer pkLayer;
	private Date date;
	private String layerType;
	private String imageRequestUrl;
	
	
	public SatelliteODTO() {
		// TODO Auto-generated constructor stub
	}
	
	public Integer getPkLayer() {
		return pkLayer;
	}

	public void setPkLayer(Integer pkLayer) {
		this.pkLayer = pkLayer;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getImageRequestUrl() {
		return imageRequestUrl;
	}

	public void setImageRequestUrl(String imageRequestUrl) {
		this.imageRequestUrl = imageRequestUrl;
	}

	public String getLayerType() {
		return layerType;
	}

	public void setLayerType(String layerType) {
		this.layerType = layerType;
	}
	
}
