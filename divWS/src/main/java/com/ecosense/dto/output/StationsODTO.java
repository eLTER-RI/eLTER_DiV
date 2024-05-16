package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.List;

import com.ecosense.dto.BoundingBoxDTO;

public class StationsODTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private BoundingBoxDTO boundingBox;
	private List<StationODTO> stations;
	
	public StationsODTO() {
		// TODO Auto-generated constructor stub
	}

	public BoundingBoxDTO getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBoxDTO boundingBox) {
		this.boundingBox = boundingBox;
	}

	public List<StationODTO> getStations() {
		return stations;
	}

	public void setStations(List<StationODTO> stations) {
		this.stations = stations;
	}
	
}
