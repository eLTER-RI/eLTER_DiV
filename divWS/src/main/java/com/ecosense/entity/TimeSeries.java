package com.ecosense.entity;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;


/**
 * The persistent class for the timeseries database table.
 * 
 */
@Entity
@Table(name="timeseries")
@NamedQuery(name="TimeSeries.findAll", query="SELECT t FROM TimeSeries t")
public class TimeSeries implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TIMESERIES_ID_GENERATOR", sequenceName="TIMESERIES_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TIMESERIES_ID_GENERATOR")
	private Integer id;

	@Column(name="sos_id")
	private Integer sosId;

	@Column(name="last_time")
	private Date lastTime;

	@Column(name="last_value")
	private Double lastValue;

	@Column(name="first_time")
	private Date firstTime;

	@Column(name="first_value")
	private Double firstValue;

	private String uom;
	
	private String label;

	//bi-directional many-to-one association to Phenomenon
	@ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST, CascadeType.REMOVE})
	private Phenomenon phenomenon;

	//bi-directional many-to-one association to Procedure
	@ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST, CascadeType.REMOVE})
	private Procedure procedure;

	//bi-directional many-to-one association to Station
	@ManyToOne
	private Station station;

	public TimeSeries() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getLastTime() {
		return this.lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	public Double getLastValue() {
		return this.lastValue;
	}

	public void setLastValue(Double lastValue) {
		this.lastValue = lastValue;
	}

	public String getUom() {
		return this.uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Phenomenon getPhenomenon() {
		return this.phenomenon;
	}

	public void setPhenomenon(Phenomenon phenomenon) {
		this.phenomenon = phenomenon;
	}

	public Procedure getProcedure() {
		return this.procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	public Station getStation() {
		return this.station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public Integer getSosId() {
		return sosId;
	}

	public void setSosId(Integer sosId) {
		this.sosId = sosId;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getFirstTime() {
		return firstTime;
	}

	public void setFirstTime(Date firstTime) {
		this.firstTime = firstTime;
	}

	public Double getFirstValue() {
		return firstValue;
	}

	public void setFirstValue(Double firstValue) {
		this.firstValue = firstValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lastValue == null) ? 0 : lastValue.hashCode());
		result = prime * result + ((firstTime == null) ? 0 : firstTime.hashCode());
		result = prime * result + ((lastTime == null) ? 0 : lastTime.hashCode());
		result = prime * result + ((firstValue == null) ? 0 : firstValue.hashCode());
		result = prime * result + ((uom == null) ? 0 : uom.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		TimeSeries other = (TimeSeries) obj;
		if (!label.equals(other.label))
			return false;
		if (!lastTime.equals(other.lastTime))
			return false;
		if (!lastValue.equals(other.lastValue))
			return false;
		if (!firstTime.equals(other.firstTime))
			return false;
		if (!firstValue.equals(other.firstValue))
			return false;
		if (!uom.equals(other.uom))
			return false;
		return true;
	}

	
	
	

}