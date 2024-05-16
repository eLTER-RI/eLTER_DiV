package com.ecosense.dto;

import java.io.Serializable;
import java.util.Date;

import com.ecosense.dto.output.PhenomenonODTO;

public class TimeSeriesDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String label;
	private Double lastValue;
	private Date lastValueDate;
	private Double firstValue;
	private Date firstValueDate;
	private String uom;
	
	private String procedure; 
	private String observedProperty; //phenomenon.domainId
	
	private Integer id;
	
	private PhenomenonODTO phenomenon;
	
	public TimeSeriesDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Double getLastValue() {
		return lastValue;
	}

	public void setLastValue(Double lastMeas) {
		this.lastValue = lastMeas;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Date getLastValueDate() {
		return lastValueDate;
	}

	public void setLastValueDate(Date lastValueDate) {
		this.lastValueDate = lastValueDate;
	}


	public String getProcedure() {
		return procedure;
	}


	public void setProcedure(String provedure) {
		this.procedure = provedure;
	}


	public String getObservedProperty() {
		return observedProperty;
	}

	public void setObservedProperty(String observedProperty) {
		this.observedProperty = observedProperty;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getFirstValue() {
		return firstValue;
	}

	public void setFirstValue(Double firstValue) {
		this.firstValue = firstValue;
	}

	public Date getFirstValueDate() {
		return firstValueDate;
	}

	public void setFirstValueDate(Date firstValueDate) {
		this.firstValueDate = firstValueDate;
	}

	public PhenomenonODTO getPhenomenon() {
		return phenomenon;
	}

	public void setPhenomenon(PhenomenonODTO phenomenon) {
		this.phenomenon = phenomenon;
	}

}
