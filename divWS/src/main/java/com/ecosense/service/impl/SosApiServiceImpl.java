package com.ecosense.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.PointDTO;
import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.TimeSeriesDTO;
import com.ecosense.dto.input.FilterStationIDTO;
import com.ecosense.dto.input.MeasurementIDTO;
import com.ecosense.dto.input.TimeseriesIDTO;
import com.ecosense.dto.output.MeasurementODTO;
import com.ecosense.dto.output.MeasurementsODTO;
import com.ecosense.dto.output.PhenomenonODTO;
import com.ecosense.dto.output.SosPathODTO;
import com.ecosense.dto.output.StationODTO;
import com.ecosense.dto.output.StationTimeSeriesODTO;
import com.ecosense.dto.output.StationsODTO;
import com.ecosense.dto.output.TimeSeriesPhenomenonODTO;
import com.ecosense.entity.Phenomenon;
import com.ecosense.entity.Procedure;
import com.ecosense.entity.SosPath;
import com.ecosense.entity.Station;
import com.ecosense.entity.TimeSeries;
import com.ecosense.exception.SimpleException;
import com.ecosense.repository.PhenomenonJpaRepo;
import com.ecosense.repository.ProcedureJpaRepo;
import com.ecosense.repository.SosApiRepo;
import com.ecosense.repository.SosPathRepo;
import com.ecosense.repository.StationJpaRepo;
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
	StationJpaRepo stationRepo;
	
	@Autowired
	TimeseriesJpaRepo timeseriesRepo;
	
	@Autowired
	PhenomenonJpaRepo phenomenonRepo;
	
	@Autowired
	ProcedureJpaRepo procedureRepo;

	@Override
	public StationsODTO getStations() throws SimpleException {
		StationsODTO resultODTO = new StationsODTO();
		List<Station> stations = stationRepo.findAll();
		List<StationODTO> stationsDTO = new ArrayList<>();
		
		BoundingBoxDTO boundingBox = new BoundingBoxDTO(Double.MAX_VALUE, Double.MAX_VALUE, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		
		for (Station station : stations) {
			StationODTO stationDTO = new StationODTO();
			stationDTO.setId(station.getId());
			stationDTO.setName(station.getTitle());
			
			Point point = (Point) station.getPoint();
			BoundingBoxDTO stationBbox = new BoundingBoxDTO(point.getEnvelopeInternal().getMinX(), point.getEnvelopeInternal().getMinY(),
					point.getEnvelopeInternal().getMaxX(), point.getEnvelopeInternal().getMaxY());
			double lng = point.getCoordinate().x;
			double lat = point.getCoordinate().y;
			stationDTO.setPoint(new PointDTO(lat, lng, stationBbox));
		
			boundingBox = Utils.setBB(stationBbox, boundingBox);
			
			stationsDTO.add(stationDTO);
		}
		resultODTO.setStations(stationsDTO);
		resultODTO.setBoundingBox(boundingBox);
		return resultODTO;
	}

	@Override
	public List<StationTimeSeriesODTO> getStationTimeSeries(Integer stationId) throws SimpleException {
		return null;
	}
	
	@Override
	public void insertIntoDB(Integer id) throws SimpleException {
		Optional<SosPath> sosPath = sosPathRepo.findById(id);
		
		if (sosPath.isEmpty()) {
			throw new SimpleException(SimpleResponseDTO.DATA_NOT_EXIST);
		}
		
		List<Procedure> procedures = sosApiRepo.getProcedures(sosPath.get().getUrl());
		for (Procedure procedure : procedures) {
			procedureRepo.saveAndFlush(procedure);
		}
		
		List<Phenomenon> phenomenons = sosApiRepo.getPhenomenons(sosPath.get().getUrl());
		for(Phenomenon phenomenon : phenomenons) {
			phenomenonRepo.saveAndFlush(phenomenon);
		}
		
		List<Station> stations = sosApiRepo.getStations(sosPath.get());
		for(Station station : stations) {
			stationRepo.saveAndFlush(station);
		}
		
		List<TimeSeries> timeSeries = sosApiRepo.getTimeSeries(sosPath.get().getUrl());
		for(TimeSeries ts : timeSeries) {
			List<Procedure> procedure = procedureRepo.getBySosIdAndLabel(ts.getProcedure().getSosId(), ts.getProcedure().getLabel());
			List<Phenomenon> phenomenon = phenomenonRepo.getBySosIdAndLabel(ts.getPhenomenon().getSosId(),ts.getPhenomenon().getLabel());
			List<Station> station = stationRepo.getBySosId(ts.getStation().getSosId(), id);
			
			ts.setPhenomenon(phenomenon.get(0));
			ts.setProcedure(procedure.get(0));
			ts.setStation(station.get(0));
			
			timeseriesRepo.saveAndFlush(ts);
		}
		
	}
	
	@Override
	public void refreshDB() throws SimpleException {
		List<SosPath> sosPaths = sosPathRepo.findAllActive();
		for (SosPath sosPath : sosPaths) {
			System.out.println("-----------------------");
			System.out.println("Sync for: " + sosPath.getUrl());
			System.out.println("-----------------------");

			List<Phenomenon> phenomenonsFromDB = phenomenonRepo.getBySosId(sosPath.getId());
			for(Phenomenon ph : phenomenonsFromDB) {
				phenomenonRepo.delete(ph);
			}
			List<Phenomenon> phenomenons = sosApiRepo.getPhenomenons(sosPath.getUrl());
			for (Phenomenon phenomenon : phenomenons) {
				phenomenonRepo.saveAndFlush(phenomenon);
			}
			
			
			List<Procedure> proceduresFromDB = procedureRepo.getBySosId(sosPath.getId());
			for (Procedure pr : proceduresFromDB) {
				procedureRepo.delete(pr);
			}
			List<Procedure> procedures = sosApiRepo.getProcedures(sosPath.getUrl());
			for (Procedure procedure : procedures) {
				procedureRepo.saveAndFlush(procedure);
			}
			
			
			List<Station> stationsFromDB = stationRepo.findStationBySosPathId(sosPath.getId());
			for(Station s : stationsFromDB) {
				stationRepo.delete(s);
			}
			List<Station> stations = sosApiRepo.getStations(sosPath);
			for (Station station : stations) {
				stationRepo.saveAndFlush(station);
			}
			
			
			
			List<TimeSeries> timeSeries = sosApiRepo.getTimeSeries(sosPath.getUrl());
			for (TimeSeries ts : timeSeries) {
				Procedure procedure = procedureRepo.getBySosIdAndLabel(ts.getProcedure().getSosId(),ts.getProcedure().getLabel()).get(0);
				Phenomenon phenomenon = phenomenonRepo.getBySosIdAndLabel(ts.getPhenomenon().getSosId(),ts.getPhenomenon().getLabel()).get(0);
				Station station  = stationRepo.getBySosId(ts.getStation().getSosId(), sosPath.getId()).get(0);
				
				ts.setPhenomenon(phenomenon);
				ts.setProcedure(procedure);
				ts.setStation(station);
				
				timeseriesRepo.saveAndFlush(ts);
			}
		}
	}
	
	@Override
	public List<SosPathODTO> getSosPaths() throws SimpleException {
		List<SosPath> sosPaths = sosPathRepo.findAll();
		List<SosPathODTO> sosPathsDTO = new ArrayList<>();
		
		for (SosPath sosPath : sosPaths) {
			SosPathODTO sosPathDTO = new SosPathODTO();
			sosPathDTO.setId(sosPath.getId());
			sosPathDTO.setUrl(sosPath.getUrl());
			sosPathDTO.setTitle(sosPath.getTitle());
			
			sosPathsDTO.add(sosPathDTO);
		}
		return sosPathsDTO;
	}
	
	@Override
	public StationsODTO filterStations(FilterStationIDTO filterStationIDTO) throws SimpleException {
		StationsODTO result = new StationsODTO();
		
		List<Station> stationsFromDB = stationRepo.filterStations(filterStationIDTO);
		List<StationODTO> stationsDTO = new ArrayList<>();
		
		BoundingBoxDTO currentBB = new BoundingBoxDTO(Double.MAX_VALUE, Double.MAX_VALUE, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		for (Station station : stationsFromDB) {
			StationODTO stationDTO = new StationODTO();
			stationDTO.setId(station.getId());
			stationDTO.setName(station.getTitle());
			
			Point point = (Point) station.getPoint();

			BoundingBoxDTO bbox = new BoundingBoxDTO(point.getEnvelopeInternal().getMinX(), point.getEnvelopeInternal().getMinY(),
					point.getEnvelopeInternal().getMaxX(), point.getEnvelopeInternal().getMaxY());
			
			double lng = point.getCoordinate().x;
			double lat = point.getCoordinate().y;
			stationDTO.setPoint(new PointDTO(lat, lng, bbox));
			
			currentBB = Utils.setBB(bbox, currentBB);
			
			stationsDTO.add(stationDTO);
		}
		
		result.setStations(stationsDTO);
		result.setBoundingBox(stationsDTO.isEmpty() ? null : currentBB);
		return result;
	}
	
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
		Station station = stationRepo.getOne(timeseriesIDTO.getStationId());
		
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
