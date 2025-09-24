package com.ecosense.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.TimeIOObservationRequestDTO;
import com.ecosense.dto.output.MeasurementsODTO;
import com.ecosense.service.TimeIOObservationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/rest/timeio/observation")
public class TimeIOObservationController {
    
    @Autowired private TimeIOObservationService timeIOObservationService;

    @RequestMapping(value = "/getObservations", method = RequestMethod.POST)
	public Response getObservations(@RequestBody TimeIOObservationRequestDTO request) {
		SimpleResponseDTO response = new SimpleResponseDTO();
        List<MeasurementsODTO> result = new ArrayList<>();
		try {
			
			result = timeIOObservationService.getObservations(request);

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
