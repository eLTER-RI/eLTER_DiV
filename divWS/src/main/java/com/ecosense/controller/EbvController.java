package com.ecosense.controller;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecosense.dto.EbvDTO;
import com.ecosense.dto.EbvDetailDTO;
import com.ecosense.dto.EbvNameDTO;
import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.input.EbvFilterIDTO;
import com.ecosense.service.EbvService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/rest/ebv")
public class EbvController {
	
	@Autowired
	EbvService ebvService;
	
	
	@RequestMapping(value = "/refresh", method = RequestMethod.GET)
	public Response refresh() {
		SimpleResponseDTO response = new SimpleResponseDTO();
		try {
			
			ebvService.refreshDataset();

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
	
	@GetMapping(value = "/get")
	public Response getCodebooks(@RequestParam List<String> codebook) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		HashMap<String, List> result = new HashMap<>();
		try {
			
			if (codebook.contains("ebvClass")) {
				result.put("ebvClass", ebvService.getAllEbvClass());
			}
			if (codebook.contains("entityType")) {
				result.put("entityType", ebvService.getAllEbvEntityType());
			}
			if (codebook.contains("ebvName")) {
				result.put("ebvName", ebvService.getAllEbvName());
			}
			if (codebook.contains("spatialScope")) {
				result.put("spatialScope", ebvService.getAllEbvSpatialScope());
			}
			if (codebook.contains("creator")) {
				result.put("creator", ebvService.getAllCreators());
			}
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
	
	@GetMapping(value = "/getDetail")
	public Response getDetail(@RequestParam Integer id) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		EbvDetailDTO result = new EbvDetailDTO();
		try {
			result = ebvService.getDetail(id);
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
	
	@GetMapping(value = "/getEbvName")
	public Response getEbvName(@RequestParam(name = "ebvClassId") Integer ebvClassId) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		List<EbvNameDTO> result = null;
		try {
			result = ebvService.getEbvName(ebvClassId);
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
	
	@PostMapping(value = "/filter")
	public Response getDetail(@RequestBody EbvFilterIDTO filter) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		List<EbvDTO> result = null;
		try {
			result = ebvService.filterEbv(filter);
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
