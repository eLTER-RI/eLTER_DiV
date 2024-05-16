package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.PointDTO;
import com.ecosense.dto.TimeSeriesDTO;

public class StationTimeSeriesODTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer stationId;
	private String title;
	private String featureOfInterest;
	private PointDTO point;
	private Boolean isProviderValid;
	
	private List<TimeSeriesPhenomenonODTO> timeseriesPhenomenon;
	
	public StationTimeSeriesODTO() {
		// TODO Auto-generated constructor stub
	}

	public Boolean getIsProviderValid() {
		return isProviderValid;
	}

	public void setIsProviderValid(Boolean isProviderValid) {
		this.isProviderValid = isProviderValid;
	}


	public Integer getStationId() {
		return stationId;
	}

	public void setStationId(Integer stationId) {
		this.stationId = stationId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFeatureOfInterest() {
		return featureOfInterest;
	}

	public void setFeatureOfInterest(String featureOfInterest) {
		this.featureOfInterest = featureOfInterest;
	}

	public List<TimeSeriesPhenomenonODTO> getTimeseriesPhenomenon() {
		return timeseriesPhenomenon;
	}

	public void setTimeseriesPhenomenon(List<TimeSeriesPhenomenonODTO> timeseriesPhenomenon) {
		this.timeseriesPhenomenon = timeseriesPhenomenon;
	}

	public PointDTO getPoint() {
		return point;
	}

	public void setPoint(PointDTO point) {
		this.point = point;
	}
	
}
