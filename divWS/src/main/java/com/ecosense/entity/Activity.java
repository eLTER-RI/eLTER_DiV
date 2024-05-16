package com.ecosense.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@NamedQuery(name="Activity.findAll", query="SELECT a FROM Activity a")
@NamedQuery(name="Activity.findAllOrderByActivityName", query="SELECT a FROM Activity a ORDER BY a.activityName")
public class Activity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	@Column(name="activity_name")
	private String activityName;

	@JsonBackReference
	@ManyToMany(mappedBy="activities", fetch = FetchType.LAZY)
	private List<Site> sites;

	public Activity() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getActivityName() {
		return this.activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
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
		Activity that = (Activity) o;
		return this.activityName.equals(that.activityName);
	}

}