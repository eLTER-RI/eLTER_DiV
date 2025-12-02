package com.ecosense.controller;

import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.timeio.TimeIOThingDTO;
import com.ecosense.service.TimeIOThingService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/rest/timeio/thing")
public class TimeIOThingController {

    @Autowired private TimeIOThingService timeIOThingService;

    @RequestMapping(value = "/getThingDetails", method = RequestMethod.GET)
	public Response getLocationDetails(@QueryParam("id") Integer id) {
		SimpleResponseDTO response = new SimpleResponseDTO();
        TimeIOThingDTO result = new TimeIOThingDTO();
		try {
			
			result = timeIOThingService.getThingDetails(id);

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
