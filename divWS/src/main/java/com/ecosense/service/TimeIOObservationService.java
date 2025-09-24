package com.ecosense.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecosense.dto.TimeIOObservationRequestDTO;
import com.ecosense.dto.output.MeasurementsODTO;

@Service
public interface TimeIOObservationService {

    List<MeasurementsODTO> getObservations(TimeIOObservationRequestDTO request) throws Exception;

}
