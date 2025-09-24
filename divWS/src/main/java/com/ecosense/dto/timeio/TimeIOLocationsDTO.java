package com.ecosense.dto.timeio;

import java.io.Serializable;
import java.util.List;

import com.ecosense.dto.BoundingBoxDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TimeIOLocationsDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private BoundingBoxDTO boundingBox;
	private List<TimeIOLocationDTO> locations;

}
