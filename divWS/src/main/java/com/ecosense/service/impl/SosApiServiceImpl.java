package com.ecosense.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.PointDTO;
import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.TimeSeriesDTO;
import com.ecosense.dto.input.MeasurementIDTO;
import com.ecosense.dto.input.TimeseriesIDTO;
import com.ecosense.dto.output.MeasurementODTO;
import com.ecosense.dto.output.MeasurementsODTO;
import com.ecosense.dto.output.PhenomenonODTO;
import com.ecosense.dto.output.StationTimeSeriesODTO;
import com.ecosense.dto.output.TimeSeriesPhenomenonODTO;
import com.ecosense.entity.Phenomenon;
import com.ecosense.entity.Station;
import com.ecosense.entity.TimeSeries;
import com.ecosense.exception.SimpleException;
import com.ecosense.repository.PhenomenonJpaRepo;
import com.ecosense.repository.ProcedureJpaRepo;
import com.ecosense.repository.SosApiRepo;
import com.ecosense.repository.SosPathRepo;
import com.ecosense.repository.TimeseriesJpaRepo;
import com.ecosense.service.SosApiService;
import com.ecosense.utils.Utils;

import org.locationtech.jts.geom.Point;

@Service
public class SosApiServiceImpl implements SosApiService {
	
	@Autowired
	SosApiRepo sosApiRepo;
	
	@Autowired
	SosPathRepo sosPathRepo;
	
	@Autowired
	TimeseriesJpaRepo timeseriesRepo;
	
	@Autowired
	PhenomenonJpaRepo phenomenonRepo;
	
	@Autowired
	ProcedureJpaRepo procedureRepo;
	
	@Override
	public List<MeasurementsODTO> getMeasurements(List<MeasurementIDTO> measurementsIDTO) throws SimpleException {
		if (measurementsIDTO == null || measurementsIDTO.isEmpty()) {
			throw new SimpleException(SimpleResponseDTO.DATA_NOT_COMPLETE);
		}
		
		List<MeasurementsODTO> measResponsesDTO = new ArrayList<>();
		
		for (MeasurementIDTO measDTO : measurementsIDTO) {
			Optional<TimeSeries> timeSeries = timeseriesRepo.findById(measDTO.getTimeseriesId());
			if (timeSeries.isEmpty()) {
				throw new SimpleException(SimpleResponseDTO.DATA_NOT_EXIST);
			}
			
			long difference;
			Date dateFrom = measDTO.getDateFrom();
			Date dateTo = measDTO.getDateTo();
			MeasurementsODTO measResponseDTO = new MeasurementsODTO();
			
			if (dateFrom == null && dateTo == null) {
				measResponseDTO =  sosApiRepo.getMeasurements(timeSeries.get(), dateFrom, dateTo);
			} else {
				Date dateFromTmp = dateTo;
				List<MeasurementODTO> measurements = new ArrayList<>();
				do {
					difference = dateTo.getTime() - dateFrom.getTime();
					 
					if (difference / (Utils.DAY) > 365) {
						 Calendar calendar = Calendar.getInstance();
						 calendar.setTime(dateTo);
						 calendar.add(Calendar.YEAR, -1);
						 
						 dateFromTmp = calendar.getTime();
					} else {
						dateFromTmp = dateFrom;
					}
					
					 measResponseDTO = sosApiRepo.getMeasurements(timeSeries.get(), dateFromTmp, dateTo);
					 measurements.addAll(measResponseDTO.getMeasurements());
					 dateTo = dateFromTmp;
				} while(difference >= 365);
				
				measResponseDTO.setMeasurements(measurements);
			}			
			if (!measResponseDTO.getMeasurements().isEmpty()) {
				measResponsesDTO.add(measResponseDTO);
			}

			Collections.sort(measResponseDTO.getMeasurements());
		}
		return measResponsesDTO;
	} 

	@Override
	public StationTimeSeriesODTO loadTimeSeries(TimeseriesIDTO timeseriesIDTO) throws SimpleException {
		if (timeseriesIDTO == null) {
			throw new SimpleException(SimpleResponseDTO.DATA_NOT_COMPLETE);
		}
		
		StationTimeSeriesODTO stationTimeSeriesODTO = new StationTimeSeriesODTO();
		Station station = null;//stationRepo.getOne(timeseriesIDTO.getStationId());
		
		if (station == null) {
			throw new SimpleException(SimpleResponseDTO.DATA_NOT_EXIST);
		}
		
		String provider = station.getSosPath().getUrl();
		System.out.println(provider);
		Boolean isValid = sosApiRepo.isProviderValid(provider);
		stationTimeSeriesODTO.setIsProviderValid(isValid);
		
		
		stationTimeSeriesODTO.setStationId(timeseriesIDTO.getStationId());
		stationTimeSeriesODTO.setTitle(station.getTitle());
		Point point = (Point) station.getPoint();
		BoundingBoxDTO stationBbox = new BoundingBoxDTO(point.getEnvelopeInternal().getMinX(), point.getEnvelopeInternal().getMinY(),
				point.getEnvelopeInternal().getMaxX(), point.getEnvelopeInternal().getMaxY());
		double lng = point.getCoordinate().x;
		double lat = point.getCoordinate().y;
		PointDTO pointDTO = new PointDTO(lat, lng, stationBbox);
		stationTimeSeriesODTO.setPoint(pointDTO);
		
		List<TimeSeriesPhenomenonODTO> timeseriesPhenomenons = new ArrayList<>();
		for (TimeSeries timeSeries : station.getTimeseries()) {
			if (timeseriesIDTO.getPhenomenLabels() != null && !timeseriesIDTO.getPhenomenLabels().isEmpty()) { // kada se ucitava station sa TS-ovima samo sa phenomenons iz filtera
				if (!timeseriesIDTO.getPhenomenLabels().contains(timeSeries.getPhenomenon().getLabel())) {
					continue;
				}
			}
			
			Phenomenon phenomenon = timeSeries.getPhenomenon();
			List<TimeSeriesDTO> timeSeriesDTOs;
			TimeSeriesPhenomenonODTO timeSeriesPhenomenon = timeseriesPhenomenons.stream()
																					.filter( tsPh ->  tsPh.getPhenomenonLabelEn().equals(phenomenon.getLabelEn()))
																					.findAny().orElse(null);
			
			if (timeSeriesPhenomenon == null) {
				timeSeriesPhenomenon = new TimeSeriesPhenomenonODTO();
				timeSeriesPhenomenon.setPhenomenonLabelEn(phenomenon.getLabelEn());
				timeSeriesDTOs = new ArrayList<>();
			} else {
				timeSeriesDTOs = timeSeriesPhenomenon.getTimeseries();
			}
			
			TimeSeriesDTO tsDTO = new TimeSeriesDTO();
			tsDTO.setLabel(timeSeries.getLabel());
			tsDTO.setLastValue(timeSeries.getLastValue());
			tsDTO.setLastValueDate(timeSeries.getLastTime());
			tsDTO.setFirstValue(timeSeries.getFirstValue());
			tsDTO.setFirstValueDate(timeSeries.getFirstTime());
			tsDTO.setObservedProperty(phenomenon.getDomainid());
			tsDTO.setProcedure(timeSeries.getProcedure().getLabel());
			tsDTO.setUom(timeSeries.getUom());
			tsDTO.setId(timeSeries.getId());
			tsDTO.setPhenomenon(new PhenomenonODTO(phenomenon.getId(),phenomenon.getLabel()));
			timeSeriesDTOs.add(tsDTO);
			
			timeSeriesPhenomenon.setTimeseries(timeSeriesDTOs);
			
			if (timeSeriesDTOs.size() == 1) {
				timeseriesPhenomenons.add(timeSeriesPhenomenon);
			}
		}
		
		Collections.sort(timeseriesPhenomenons);
		stationTimeSeriesODTO.setTimeseriesPhenomenon(timeseriesPhenomenons);
		return stationTimeSeriesODTO;
	}
	
	
	
	@Override
	public List<PhenomenonODTO> getPhenomenons() throws SimpleException {
		List<String> phenomenons = phenomenonRepo.findPhenomenonsDistinct();
		List<PhenomenonODTO> phenomenonsDTO = new ArrayList<>();
		
		for (String phenomenon : phenomenons) {
				PhenomenonODTO phenomenonODTO = new PhenomenonODTO();
				phenomenonODTO.setLabel(phenomenon);
				phenomenonsDTO.add(phenomenonODTO);
		}
		return phenomenonsDTO;
	}
	
}
