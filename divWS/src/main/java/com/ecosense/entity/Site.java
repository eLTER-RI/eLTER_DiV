package com.ecosense.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.locationtech.jts.geom.Geometry;

@Entity  
@NamedQuery(name="Site.findAll", query="SELECT s FROM Site s")
@Table(name="site")
@Getter
@Setter
@NoArgsConstructor
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

	@JsonIgnore
	@Column(name="circle")
	private Geometry circle;
	
	@Column(name="title")
	private String title;
	
	@Column(name="area")
	private Double area;

	@Column(name="last_checked")
	private Date lastChecked;
	
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
	
	public Site(Integer id,  String idSuffix, String title, Geometry point) {
		super();
		this.id = id;
		this.idSuffix = idSuffix;
		this.point = point;
		this.title = title;
	}

	@Override
	public String toString() {
		return "Site [id=" + id + ", changed=" + changed + ", idSuffix=" + idSuffix + ", point=" + point + ", polygon="
				+ polygon + ", title=" + title + "]";
	}

	public List<Activity> getActivities() {
		if (activities == null) {
			activities = new ArrayList<>();
		}
		return activities;
	}

	public List<Country> getCountries() {
		if (countries == null) {
			countries = new ArrayList<>();
		}
		return countries;
	}

}
