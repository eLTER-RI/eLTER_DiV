package com.ecosense.dto.input;

import java.io.Serializable;
import java.util.Date;

public class MeasurementIDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Integer timeseriesId;
    private Date dateFrom;
    private Date dateTo;
	
	public MeasurementIDTO() {
		// TODO Auto-generated constructor stub
	}

	public Integer getTimeseriesId() {
		return timeseriesId;
	}

	public void setTimeseriesId(Integer timeseriesId) {
		this.timeseriesId = timeseriesId;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

}
