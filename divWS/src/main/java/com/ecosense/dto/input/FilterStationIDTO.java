package com.ecosense.dto.input;

import java.io.Serializable;
import java.util.List;

public class FilterStationIDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<String> sosPathUrls;
	private List<String> phenomenonsLabels;
	
	public FilterStationIDTO() { }

	public List<String> getSosPathUrls() {
		return sosPathUrls;
	}

	public void setSosPathUrls(List<String> sosPathUrls) {
		this.sosPathUrls = sosPathUrls;
	}

	public List<String> getPhenomenonsLabels() {
		return phenomenonsLabels;
	}

	public void setPhenomenonsLabels(List<String> phenomenonsLabels) {
		this.phenomenonsLabels = phenomenonsLabels;
	}

}
