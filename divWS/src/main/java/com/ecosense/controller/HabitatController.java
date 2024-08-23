package com.ecosense.controller;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ecosense.dto.HabitatDTO;
import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.exception.SimpleException;
import com.ecosense.service.HabitatService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/rest/habitat")
public class HabitatController {
    
    @Autowired
	private HabitatService habitatService;

    @RequestMapping(value = "/getAll", method = RequestMethod.GET,
			        produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	public Response getAll() {
		SimpleResponseDTO response = new SimpleResponseDTO();
		List<HabitatDTO> habitats = null;
		try {
			
			habitats = habitatService.getAll();

		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(habitats).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}

}
