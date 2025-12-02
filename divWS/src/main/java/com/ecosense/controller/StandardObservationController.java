package com.ecosense.controller;

import java.util.List;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.StandardObservationDTO;
import com.ecosense.exception.SimpleException;
import com.ecosense.service.StandardObservationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/rest/standardObservation")
public class StandardObservationController {
    
    @Autowired
	private StandardObservationService standardObservationService;

    @RequestMapping(value = "/getAll", method = RequestMethod.GET,
			        produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	public Response getAll() {
		SimpleResponseDTO response = new SimpleResponseDTO();
		List<StandardObservationDTO> standardObservations = null;
		try {
			
			standardObservations = standardObservationService.getAll();

		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(standardObservations).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}

}
