package com.ecosense.dto.input;

import java.io.Serializable;
import java.util.List;

public class TimeseriesIDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Integer stationId;
	private List<String> phenomenLabels;
	
	public TimeseriesIDTO() { }
	
	public Integer getStationId() {
		return stationId;
	}
	
	public void setStationId(Integer stationId) {
		this.stationId = stationId;
	}

	public List<String> getPhenomenLabels() {
		return phenomenLabels;
	}

	public void setPhenomenLabels(List<String> phenomenLabels) {
		this.phenomenLabels = phenomenLabels;
	}

	
	
	
}
