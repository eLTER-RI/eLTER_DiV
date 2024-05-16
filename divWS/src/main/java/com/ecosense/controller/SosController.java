package com.ecosense.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.input.FilterStationIDTO;
import com.ecosense.dto.input.MeasurementIDTO;
import com.ecosense.dto.input.TimeseriesIDTO;
import com.ecosense.dto.output.MeasurementsODTO;
import com.ecosense.dto.output.PhenomenonODTO;
import com.ecosense.dto.output.SosPathODTO;
import com.ecosense.dto.output.StationTimeSeriesODTO;
import com.ecosense.dto.output.StationsODTO;
import com.ecosense.exception.SimpleException;
import com.ecosense.service.SosApiService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/rest/sos")
public class SosController {
	
	@Autowired
	SosApiService sosApiService;
	
	@RequestMapping(value = "/getAllStations", method = RequestMethod.GET)
	public Response getAllStations() {
		SimpleResponseDTO response = new SimpleResponseDTO();
		StationsODTO stations = null;
		try {
			stations = sosApiService.getStations();

		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(stations).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
	@RequestMapping(value = "/getStationTimeSeries", method = RequestMethod.GET)
	public Response getStationTimeSeries(@RequestParam("id") Integer stationId) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		List<StationTimeSeriesODTO> stations = null;
		try {
			stations = sosApiService.getStationTimeSeries(stationId);

		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(stations).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
	@RequestMapping(value = "/insertIntoDatabase", method = RequestMethod.GET)
	public Response insertIntoDatabase(@RequestParam("id") Integer id) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		try {
			sosApiService.insertIntoDB(id);

		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok().build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
	@RequestMapping(value = "/getAllSosPath", method = RequestMethod.GET)
	public Response insertIntoDatabase() {
		SimpleResponseDTO response = new SimpleResponseDTO();
		List<SosPathODTO> sosPathsDTO = new ArrayList<>();
		try {
			
			sosPathsDTO = sosApiService.getSosPaths();

		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(sosPathsDTO, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
	@RequestMapping(value="/filter", method = RequestMethod.POST, 
			produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	public Response filter(@RequestBody FilterStationIDTO filterStationIDTO) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		StationsODTO stations = null;
		try {

			stations = sosApiService.filterStations(filterStationIDTO);
		
		} catch(SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch(Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(stations, MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
	
	@RequestMapping(value = "/refresh", method = RequestMethod.GET)
	public Response rf() {
		SimpleResponseDTO response = new SimpleResponseDTO();
		try {
			System.out.println("----------- SOS sync start -----------");
			sosApiService.refreshDB();
			System.out.println("----------- SOS sync end -----------");

		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok().build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
	@RequestMapping(value = "/loadTimeSeries", method = RequestMethod.POST)
	public Response loadTimeSeries(@RequestBody TimeseriesIDTO timeseriesIDTO) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		StationTimeSeriesODTO timeSeries = null;
		try {
			timeSeries = sosApiService.loadTimeSeries(timeseriesIDTO);

		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(timeSeries).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
	@RequestMapping(value = "/allPhenomenons", method = RequestMethod.GET)
	public Response getPhenomenons() {
		SimpleResponseDTO response = new SimpleResponseDTO();
		List<PhenomenonODTO> phenomenons = new ArrayList<>();
		try {
			
			phenomenons = sosApiService.getPhenomenons();

		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(phenomenons).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
	@RequestMapping(value = "/getMeasurements", method = RequestMethod.POST) 
	public Response getMeasurements(@RequestBody List<MeasurementIDTO> measurementIDTO) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		List<MeasurementsODTO> measResponseDTO = new ArrayList<MeasurementsODTO>();
		try {
			
			measResponseDTO = sosApiService.getMeasurements(measurementIDTO);
			
		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(measResponseDTO).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
}
