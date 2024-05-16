package com.ecosense.dto.input;

import java.io.Serializable;
import java.util.List;

public class FilterSiteIDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String title;
	private List<Integer> countriesId;
	private List<Integer> activitiesId;
	
	public FilterSiteIDTO() { }
 
	public String getTitle() {
		return title;
	}

	public void setTitle(String titile) {
		this.title = titile;
	}

	public List<Integer> getCountriesId() {
		return countriesId;
	}

	public void setCountriesId(List<Integer> countriesId) {
		this.countriesId = countriesId;
	}

	public List<Integer> getActivitiesId() {
		return activitiesId;
	}

	public void setActivitiesId(List<Integer> activitiesId) {
		this.activitiesId = activitiesId;
	}
	
}
