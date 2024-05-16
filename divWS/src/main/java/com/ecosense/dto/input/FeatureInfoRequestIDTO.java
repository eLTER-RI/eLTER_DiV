package com.ecosense.dto.input;

import java.io.Serializable;

import com.ecosense.dto.LayerDTO;
import com.ecosense.dto.PointDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class FeatureInfoRequestIDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private PointDTO latLng;
	private LayerDTO layer;
	private String type;
	
	public FeatureInfoRequestIDTO() {
		// TODO Auto-generated constructor stub
	}

	public PointDTO getLatLng() {
		return latLng;
	}

	public LayerDTO getLayer() {
		return layer;
	}

	public void setLatLng(PointDTO latLng) {
		this.latLng = latLng;
	}

	public void setLayer(LayerDTO layer) {
		this.layer = layer;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}
