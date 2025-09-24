package com.ecosense.dto.input;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MeasurementIDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Integer timeseriesId;
    private Date dateFrom;
    private Date dateTo;

}
