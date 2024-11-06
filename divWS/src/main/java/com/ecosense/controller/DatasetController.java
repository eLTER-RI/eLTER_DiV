package com.ecosense.controller;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ecosense.dto.DivFilterDTO;
import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.output.DatasetsODTO;
import com.ecosense.exception.SimpleException;
import com.ecosense.service.DatasetService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/rest/dataset")
public class DatasetController {
    
        @Autowired
	    private DatasetService datasetService;

    	@RequestMapping(value="/filterAndSearch", method = RequestMethod.POST, 
					produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
		public Response filterAndSearchDataset(@RequestBody DivFilterDTO divFilterDTO) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		DatasetsODTO datasets = null;
		try {
			
			datasets = datasetService.filterAndSearchDataset(divFilterDTO);

		} catch(SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch(Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(datasets, MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}

}
