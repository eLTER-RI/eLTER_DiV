package com.ecosense.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class DivFilterDTO implements Serializable {
	private static final long serialVersionUID = 1L;
    
    private List<String> sites;
    private List<String> standardObservations;
    private List<String> habitats;

    private String searchText;

    public DivFilterDTO() { }

    public List<String> getSites() {
        return sites;
    }

    public void setSites(List<String> sites) {
        this.sites = sites;
    }

    public List<String> getStandardObservations() {
        return standardObservations;
    }

    public void setStandardObservations(List<String> standardObservations) {
        this.standardObservations = standardObservations;
    }

    public List<String> getHabitats() {
        return habitats;
    }

    public void setHabitats(List<String> habitats) {
        this.habitats = habitats;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

}
