package com.ecosense.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecosense.dto.CapabilitiesRequestDTO;
import com.ecosense.dto.DivFilterDTO;
import com.ecosense.dto.LayerDTO;
import com.ecosense.dto.DatasetLayerDTO;
import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.input.FeatureInfoRequestIDTO;
import com.ecosense.dto.output.LayerGroupDTO;
import com.ecosense.exception.SimpleException;
import com.ecosense.service.LayerService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/rest/layer")
public class LayerController {
	
	@Autowired
	LayerService layerService;
	
	
	@RequestMapping(value = "/getAllLayers", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	public Response getAllLayers(@RequestParam(required = false) List<String> layertype,
			 @RequestParam(required = false) List<Integer> ids, @RequestParam(required = false) String code) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		List<LayerDTO> layers = null;
		try {
			
			layers = layerService.getAll(layertype, ids, code);

		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(layers).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
	
	@RequestMapping(value = "/getLayerGroups", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	public Response getLayerGroups(@RequestParam(required = false) List<String> layertype, @RequestParam(required = false) Integer layergroupId) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		List<LayerGroupDTO> layers = null;
		try {
			
			layers = layerService.getLayerGroups(layertype, layergroupId);

		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(layers).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
	
	@RequestMapping(value = "/getFeatureInfoHTML", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	public Response getFeatureInfoHTML(@RequestBody FeatureInfoRequestIDTO featureInfoRequestIDTO) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		String featureInfoResponse = null;
		try {
			
			featureInfoResponse = layerService.getFeatureInfoHTML(featureInfoRequestIDTO);

		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(featureInfoResponse).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
	@RequestMapping(value = "/addTimeForSnowCover", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON)
	public Response addTimeForSnowCover() {
		SimpleResponseDTO response = new SimpleResponseDTO();
		
		Boolean ok = false;
		try {
			
			ok = layerService.addTimeForSnowCover();

		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(ok).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
	@RequestMapping(value = "/getCapabilities", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON)
	public Response getCapabilities(@RequestBody CapabilitiesRequestDTO capabilitiesRequestDTO) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		
		String getCapabilitiesResponse = null;
		try {
			
			getCapabilitiesResponse = layerService.getCapabilities(capabilitiesRequestDTO);

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(getCapabilitiesResponse).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}

	@RequestMapping(value = "/filterAndSearch", method = RequestMethod.POST,
					produces = MediaType.APPLICATION_JSON)
	public Response filterAndSearch(@RequestBody DivFilterDTO divFilterDTO) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		
		List<DatasetLayerDTO> layersWithDatasets = new ArrayList<>();
		try {
			
			layersWithDatasets = layerService.filterAndSearch(divFilterDTO);

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(layersWithDatasets).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}

	@RequestMapping(value = "/searchLayerGroupsByDatasetData", method = RequestMethod.GET,
					produces = MediaType.APPLICATION_JSON)
	public Response searchLayerGroupsByDatasetData(@RequestParam String search, @RequestParam String layerType) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		
		List<LayerGroupDTO> layerGroups = new ArrayList<>();
		try {
			
			layerGroups = layerService.searchLayerGroupsByDatasetData(search, layerType);

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(layerGroups).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}

}
