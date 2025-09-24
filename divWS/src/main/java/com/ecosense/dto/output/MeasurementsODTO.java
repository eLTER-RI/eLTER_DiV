package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MeasurementsODTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<MeasurementODTO> measurements;
	private PhenomenonODTO phenomenon;
	private String uom;
	private String station;
	private String procedure;
	private Long timeseriesId;
	
}
