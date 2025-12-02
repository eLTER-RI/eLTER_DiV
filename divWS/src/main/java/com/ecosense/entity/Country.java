package com.ecosense.entity;

import java.io.Serializable;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQuery(name="Country.findAll", query="SELECT c FROM Country c")
@NamedQuery(name="Country.findAllOrderByCountryName", query="SELECT c FROM Country c ORDER BY c.countryName")
public class Country implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="COUNTRY_ID_GENERATOR", sequenceName="COUNTRY_SEQ", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="COUNTRY_ID_GENERATOR")
	private Integer id;

	@Column(name="country_name")
	private String countryName;

	@JsonBackReference
	@ManyToMany(mappedBy="countries", fetch = FetchType.LAZY)
	private List<Site> sites;

	public Country() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public List<Site> getSites() {
		if (this.sites == null) {
			this.sites = new ArrayList<>();
		}
		return this.sites;
	}

	public void setSites(List<Site> sites) {
		this.sites = sites;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) 
			return false;
		Country that = (Country) o;
		return this.countryName.equals(that.countryName);
	}
	
}