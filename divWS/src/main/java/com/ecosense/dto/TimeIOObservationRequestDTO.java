package com.ecosense.dto;

import java.io.Serializable;
import java.util.List;

import com.ecosense.dto.input.MeasurementIDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TimeIOObservationRequestDTO implements Serializable {
    
	private static final long serialVersionUID = 1L;

    String phenomenonId;
    
    List<MeasurementIDTO> measurementRequests;

}
