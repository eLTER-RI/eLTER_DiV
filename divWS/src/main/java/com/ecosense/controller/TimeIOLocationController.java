package com.ecosense.controller;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.timeio.TimeIOLocationDTO;
import com.ecosense.dto.timeio.TimeIOLocationsDTO;
import com.ecosense.service.TimeIOLocationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/rest/timeio/location")
public class TimeIOLocationController {

    @Autowired private TimeIOLocationService timeIOLocationService;

    @RequestMapping(value = "/getAllLocations", method = RequestMethod.GET)
	public Response getAllLocations() {
		SimpleResponseDTO response = new SimpleResponseDTO();
        TimeIOLocationsDTO result = new TimeIOLocationsDTO();
		try {
			
			result = timeIOLocationService.getAllLocations();

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(result).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}

	@RequestMapping(value = "/getLocationDetails", method = RequestMethod.GET)
	public Response getLocationDetails(@QueryParam("id") Integer id) {
		SimpleResponseDTO response = new SimpleResponseDTO();
        TimeIOLocationDTO result = new TimeIOLocationDTO();
		try {
			
			result = timeIOLocationService.getLocationDetails(id);

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(result).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}	

}
