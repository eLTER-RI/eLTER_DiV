package com.ecosense.controller;

import jakarta.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.output.HtmlDto;
import com.ecosense.service.UtilService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/rest")
public class UtilController {
	
	@Autowired
	UtilService utilService;
	
	@RequestMapping(value = "/getHtml", method = RequestMethod.GET)
	public Response getHtml(@RequestParam String partOfApp) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		HtmlDto html = null;
		try {
			
			html = utilService.getHtml(partOfApp);
			

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(html).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}

}
