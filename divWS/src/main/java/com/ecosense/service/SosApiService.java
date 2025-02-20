package com.ecosense.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecosense.dto.input.MeasurementIDTO;
import com.ecosense.dto.input.TimeseriesIDTO;
import com.ecosense.dto.output.MeasurementsODTO;
import com.ecosense.dto.output.PhenomenonODTO;
import com.ecosense.dto.output.StationTimeSeriesODTO;
import com.ecosense.exception.SimpleException;

@Service
public interface SosApiService {

	List<MeasurementsODTO> getMeasurements(List<MeasurementIDTO> measurementsIDTO) throws SimpleException;

	StationTimeSeriesODTO loadTimeSeries(TimeseriesIDTO timeseriesIDTO) throws SimpleException;

	List<PhenomenonODTO> getPhenomenons() throws SimpleException;

}
