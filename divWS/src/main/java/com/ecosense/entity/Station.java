package com.ecosense.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.locationtech.jts.geom.Geometry;


/**
 * The persistent class for the station database table.
 * 
 */

@Entity
@NamedQuery(name="Station.findAll", query="SELECT s FROM Station s")
public class Station implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="STATION_ID_GENERATOR", sequenceName="STATION_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="STATION_ID_GENERATOR")
	private Integer id;

	@Column(name="feature_of_interest")
	private String featureOfInterest;

	private Geometry point;
	
	@Column(name="sos_id")
	private Integer sosId;

	private String title;

	//bi-directional many-to-one association to SosPath
	@ManyToOne
	@JoinColumn(name="sos_path_id")
	private SosPath sosPath;

	//bi-directional many-to-one association to Timesery
	@OneToMany(mappedBy="station", cascade = {CascadeType.MERGE,CascadeType.PERSIST, CascadeType.REMOVE})
	private List<TimeSeries> timeseries;

	public Station() {
	}
	
	public Station(Integer sosId) {
		this.sosId = sosId;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFeatureOfInterest() {
		return this.featureOfInterest;
	}

	public void setFeatureOfInterest(String featureOfInterest) {
		this.featureOfInterest = featureOfInterest;
	}

	public Geometry getPoint() {
		return this.point;
	}

	public void setPoint(Geometry point) {
		this.point = point;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public SosPath getSosPath() {
		return this.sosPath;
	}

	public void setSosPath(SosPath sosPath) {
		this.sosPath = sosPath;
	}

	public List<TimeSeries> getTimeseries() {
		return this.timeseries;
	}

	public void setTimeseries(List<TimeSeries> timeseries) {
		this.timeseries = timeseries;
	}

	public TimeSeries addTimesery(TimeSeries timesery) {
		getTimeseries().add(timesery);
		timesery.setStation(this);

		return timesery;
	}

	public TimeSeries removeTimesery(TimeSeries timesery) {
		getTimeseries().remove(timesery);
		timesery.setStation(null);

		return timesery;
	}

	public Integer getSosId() {
		return sosId;
	}

	public void setSosId(Integer sosId) {
		this.sosId = sosId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((featureOfInterest == null) ? 0 : featureOfInterest.hashCode());
		result = prime * result + ((point == null) ? 0 : point.hashCode());
		result = prime * result + ((sosPath == null) ? 0 : sosPath.hashCode());
//		result = prime * result + ((timeseries == null) ? 0 : timeseries.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		Station other = (Station) obj;
		if (!featureOfInterest.equals(other.featureOfInterest))
			return false;
		if (!point.equals(other.point))
			return false;
		if (!title.equals(other.title))
			return false;
		
//		boolean ok = Arrays.equals(timeseries.toArray(), other.timeseries.toArray());
//		if (!ok)
//			return false;
		
		return true;
	}
	
	
	

}