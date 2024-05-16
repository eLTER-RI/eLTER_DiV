package com.ecosense.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.locationtech.jts.geom.Geometry;
import org.teiid.core.types.GeographyType;

@Entity  
@NamedQuery(name="Site.findAll", query="SELECT s FROM Site s")
@Table(name="site")
public class Site implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="SITE_ID_GENERATOR", sequenceName="SITE_SEQ", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SITE_ID_GENERATOR")
	@Column(name="id")
	private Integer id;
	
	@Column(name="changed")
	private Date changed;
	
	@Column(name="id_suffix")
	private String idSuffix;
	
	@Column(name="point")
	private Geometry point;
	
	@JsonIgnore
	@Column(name="polygon")
	private Geometry polygon;
	
	@Column(name="title")
	private String title;
	
	@Column(name="area")
	private Double area;
	
	@JsonManagedReference
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE}, fetch = FetchType.LAZY)
	@JoinTable(
		name="site_activity"
		, joinColumns={
			@JoinColumn(name="site_id")
			}
		, inverseJoinColumns={
			@JoinColumn(name="activity_id")
			}
		)
	private List<Activity> activities;
	
	@JsonManagedReference
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE}, fetch = FetchType.LAZY)
	@JoinTable(
		name="site_country"
		, joinColumns={
			@JoinColumn(name="site_id")
			}
		, inverseJoinColumns={
			@JoinColumn(name="country_id")
			}
		)
	private List<Country> countries;
	
	public Site() {
	}
	
	public Site(Integer id,  String idSuffix, String title, Geometry point) {
		super();
		this.id = id;
		this.idSuffix = idSuffix;
		this.point = point;
		this.title = title;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getIdSuffix() {
		return this.idSuffix;
	}

	public void setIdSuffix(String idSuffix) {
		this.idSuffix = idSuffix;
	}
	
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public Date getChanged() {
		return this.changed;
	}

	public void setChanged(Date changed) {
		this.changed = changed;
	}

	public Geometry getPoint() {
		return this.point;
	}

	public void setPoint(Geometry point) {
		this.point = point;
	}

	public Geometry getPolygon() {
		return this.polygon;
	}

	public void setPolygon(Geometry polygon) {
		this.polygon = polygon;
	}
	
	public List<Activity> getActivities() {
		if (this.activities == null) {
			this.activities = new ArrayList<>();
		}
		return this.activities;
	}

	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}

	public List<Country> getCountries() {
		if (this.countries == null) {
			this.countries = new ArrayList<>();
		}
		return this.countries;
	}

	public void setCountries(List<Country> countries) {
		this.countries = countries;
	}
	
	public Double getArea() {
		return area;
	}

	public void setArea(Double area) {
		this.area = area;
	}

	@Override
	public String toString() {
		return "Site [id=" + id + ", changed=" + changed + ", idSuffix=" + idSuffix + ", point=" + point + ", polygon="
				+ polygon + ", title=" + title + "]";
	}

	
}
