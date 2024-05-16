package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.List;

public class MeasurementsODTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<MeasurementODTO> measurements;
	private PhenomenonODTO phenomenon;
	private String uom;
	private String station;
	private String procedure;
	private Integer timeseriesId;
	
	public MeasurementsODTO() { }
	
	public List<MeasurementODTO> getMeasurements() {
		return measurements;
	
	}
	public void setMeasurements(List<MeasurementODTO> measurements) {
		this.measurements = measurements;
	}
	
	public PhenomenonODTO getPhenomenon() {
		return phenomenon;
	}
	
	public void setPhenomenon(PhenomenonODTO phenomenon) {
		this.phenomenon = phenomenon;
	}
	
	public String getUom() {
		return uom;
	}
	
	public void setUom(String uom) {
		this.uom = uom;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getProcedure() {
		return procedure;
	}

	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}

	public Integer getTimeseriesId() {
		return timeseriesId;
	}

	public void setTimeseriesId(Integer timeseriesId) {
		this.timeseriesId = timeseriesId;
	}
	
}
