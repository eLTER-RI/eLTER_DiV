package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.List;

import com.ecosense.dto.TimeSeriesDTO;

public class TimeSeriesPhenomenonODTO implements Serializable, Comparable<TimeSeriesPhenomenonODTO> {
	private static final long serialVersionUID = 1L;
	
	private List<TimeSeriesDTO> timeseries;
	private String phenomenonLabelEn;
	
	public TimeSeriesPhenomenonODTO() {	}

	public List<TimeSeriesDTO> getTimeseries() {
		return timeseries;
	}

	public void setTimeseries(List<TimeSeriesDTO> timeseries) {
		this.timeseries = timeseries;
	}

	public String getPhenomenonLabelEn() {
		return phenomenonLabelEn;
	}

	public void setPhenomenonLabelEn(String phenomenonLabelEn) {
		this.phenomenonLabelEn = phenomenonLabelEn;
	}

	@Override
	public int compareTo(TimeSeriesPhenomenonODTO o) {
		return phenomenonLabelEn.compareTo(o.getPhenomenonLabelEn());
	}
	

}
