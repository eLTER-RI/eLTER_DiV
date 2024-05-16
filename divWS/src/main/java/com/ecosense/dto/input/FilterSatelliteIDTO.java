package com.ecosense.dto.input;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.ecosense.dto.BoundingBoxDTO;

public class FilterSatelliteIDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
    private String dFrom;
    private String dTo;
    private Integer srid;
    private List<String> layerType;
    private BoundingBoxDTO previewBox;
	
	public FilterSatelliteIDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public List<String> getLayerType() {
		return layerType;
	}

	public void setLayerType(List<String> layerType) {
		this.layerType = layerType;
	}

	public String getdFrom() {
		return dFrom;
	}

	public void setdFrom(String dFrom) {
		this.dFrom = dFrom;
	}

	public String getdTo() {
		return dTo;
	}

	public void setdTo(String dTo) {
		this.dTo = dTo;
	}

	public BoundingBoxDTO getPreviewBox() {
		return previewBox;
	}

	public void setPreviewBox(BoundingBoxDTO previewBox) {
		this.previewBox = previewBox;
	}
	
	public Integer getSrid() {
		return srid;
	}

	public void setSrid(Integer srid) {
		this.srid = srid;
	}

	@Override
	public String toString() {
		return "FilterSatelliteIDTO [dFrom=" + dFrom + ", dTo=" + dTo + ", layerType=" + layerType.toString() + ", previewBox="
				+ previewBox.toString() + "]";
	}
	
	

}
