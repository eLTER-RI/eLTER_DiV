package com.ecosense.controller;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.input.FilterSatelliteIDTO;
import com.ecosense.dto.output.SatelliteODTO;
import com.ecosense.exception.SimpleException;
import com.ecosense.service.SatelliteService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/rest/satellite")
public class SatelliteController {
	
	@Autowired
	SatelliteService satelliteService;
	
	/**
	 * By given date range, bbox and layer types list (all are optional) returns a list of satellite images 
	 * from GeoServer (use satellite API as intermediate layer). 
	 */
	@RequestMapping(value = "/getMapList", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	public Response getMapList(@RequestBody FilterSatelliteIDTO filterSatelliteIDTO,@RequestParam("siteId") Integer siteId) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		List<SatelliteODTO> satellite = null;
		try {
			
			satellite = satelliteService.getMapList(filterSatelliteIDTO, siteId);

		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(satellite).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
	@RequestMapping(value = "/image/one", method = RequestMethod.GET)
	public byte[] getImage(@RequestParam(value = "siteId", required=false) Integer siteId, @RequestParam("pkLayer") Integer pkLayer, 
			@RequestParam(value = "keepAspectRatio", required=false) Boolean keepAspectRatio, @RequestParam("maxx") Double maxX, 
			@RequestParam("maxy") Double maxY, @RequestParam("minx") Double minX,@RequestParam("miny") Double minY) {
		byte[] response = null;
			
		try {
			response = satelliteService.getImage(siteId, pkLayer, keepAspectRatio, maxX, maxY, minX,minY);
		} catch (SimpleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
	
	@RequestMapping(value = "/image/download", method = RequestMethod.GET)
	public Response imageDownload(@RequestParam(value = "siteId", required=false) Integer siteId, @RequestParam("pkLayer") Integer pkLayer, 
			@RequestParam(value = "keepAspectRatio", required=false) Boolean keepAspectRatio, @RequestParam("maxx") Double maxX, 
			@RequestParam("maxy") Double maxY, @RequestParam("minx") Double minX,@RequestParam("miny") Double minY) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		byte[] responseBytes = null;
			
		try {
			responseBytes = satelliteService.getImage(siteId, pkLayer, keepAspectRatio, maxX, maxY, minX,minY);
		} catch (SimpleException e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(responseBytes).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
}
