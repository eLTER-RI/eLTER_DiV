package com.ecosense.dto;

import java.io.Serializable;

public class BoundingBoxDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Double minX;
	private Double minY;
	private Double maxX;
	private Double maxY;
	
	public BoundingBoxDTO() { }
	
	public BoundingBoxDTO(Double minX, Double minY, Double maxX, Double maxY) {
		super();
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	public Double getMinX() {
		return minX;
	}

	public void setMinX(Double minX) {
		this.minX = minX;
	}

	public Double getMinY() {
		return minY;
	}

	public void setMinY(Double minY) {
		this.minY = minY;
	}

	public Double getMaxX() {
		return maxX;
	}

	public void setMaxX(Double maxX) {
		this.maxX = maxX;
	}

	public Double getMaxY() {
		return maxY;
	}

	public void setMaxY(Double maxY) {
		this.maxY = maxY;
	}

	@Override
	public String toString() {
		return "BoundingBoxDTO [minX=" + minX + ", minY=" + minY + ", maxX=" + maxX + ", maxY=" + maxY + "]";
	}
	
	
	
	

}
