package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.Date;

public class MeasurementODTO implements Serializable, Comparable<MeasurementODTO> {
	private static final long serialVersionUID = 1L;

	private Date date;
	private Double value;
	
	public MeasurementODTO() { }
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Double getValue() {
		return value;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
	

	@Override
	public int compareTo(MeasurementODTO o) {
		return date.compareTo(o.date);
	}
	
	
	
}
