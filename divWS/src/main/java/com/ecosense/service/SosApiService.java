package com.ecosense.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecosense.dto.input.FilterStationIDTO;
import com.ecosense.dto.input.MeasurementIDTO;
import com.ecosense.dto.input.TimeseriesIDTO;
import com.ecosense.dto.output.MeasurementsODTO;
import com.ecosense.dto.output.PhenomenonODTO;
import com.ecosense.dto.output.SosPathODTO;
import com.ecosense.dto.output.StationTimeSeriesODTO;
import com.ecosense.dto.output.StationsODTO;
import com.ecosense.exception.SimpleException;

@Service
public interface SosApiService {

	StationsODTO getStations() throws SimpleException;

	List<StationTimeSeriesODTO> getStationTimeSeries(Integer stationId) throws SimpleException;

	List<SosPathODTO> getSosPaths() throws SimpleException;
	
	StationsODTO filterStations(FilterStationIDTO filterStationIDTO) throws SimpleException;

	List<MeasurementsODTO> getMeasurements(List<MeasurementIDTO> measurementsIDTO) throws SimpleException;

	StationTimeSeriesODTO loadTimeSeries(TimeseriesIDTO timeseriesIDTO) throws SimpleException;

	List<PhenomenonODTO> getPhenomenons() throws SimpleException;

	void insertIntoDB(Integer id) throws SimpleException;

	void refreshDB() throws SimpleException;

}
