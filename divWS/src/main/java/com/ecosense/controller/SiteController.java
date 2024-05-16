package com.ecosense.controller;

import java.util.ArrayList;
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

import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.input.FilterSiteIDTO;
import com.ecosense.dto.output.ActivityODTO;
import com.ecosense.dto.output.CountryODTO;
import com.ecosense.dto.output.SitesODTO;
import com.ecosense.dto.output.SiteDetailsODTO;
import com.ecosense.exception.SimpleException;
import com.ecosense.service.SiteService;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/rest/site")
public class SiteController {

	@Autowired
	private SiteService siteService;
	
	@RequestMapping(value = "/refresh", method = RequestMethod.GET)
	public Response refresh() {
		SimpleResponseDTO response = new SimpleResponseDTO();
		try {
			siteService.refreshDatabase();

		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
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
	
	@RequestMapping(value = "/addAllToDB", method = RequestMethod.GET)
	public Response addAllToDB() {
		SimpleResponseDTO response = new SimpleResponseDTO();
		try {
			siteService.addAllToDB();

		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
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
	
	
	@RequestMapping(value = "/getSiteDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public Response getSiteInfo(@QueryParam("siteId") Integer siteId) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		SiteDetailsODTO siteInfoDTO = new SiteDetailsODTO();
		try {
			siteInfoDTO = siteService.getSiteInfo(siteId);

		} catch (SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(siteInfoDTO, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
	@RequestMapping(value="/getGeoJsonPolygon", method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON)
	public Response getGeoJsonPolygon(@RequestParam("id") Integer id) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		String sitePolygon = null;
		try {
			
			sitePolygon = siteService.getGeoJsonPolygon(id);
		
		} catch(Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(sitePolygon, MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}	
	 
	@RequestMapping(value="/getAllPolygons", method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON)
	public Response getAllPolygons() {
		SimpleResponseDTO response = new SimpleResponseDTO();
		SitesODTO sitesDTO = new SitesODTO();
		try {
			
			sitesDTO = siteService.getAllPolygons();
		
		} catch(SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch(Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(sitesDTO, MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}	
	
	@RequestMapping(value="/getAllActivities", method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON)
	public Response getAllActivities() {
		SimpleResponseDTO response = new SimpleResponseDTO();
		List<ActivityODTO> activitiesDTO = new ArrayList<>();
		try {
			
			activitiesDTO = siteService.getAllActivities();
		
		} catch(SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch(Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(activitiesDTO, MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
	@RequestMapping(value="/getAllCountries", method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON)
	public Response getAllCountries() {
		SimpleResponseDTO response = new SimpleResponseDTO();
		List<CountryODTO> countriesDTO = new ArrayList<>();
		try {
			
			countriesDTO = siteService.getAllCountries();
		
		} catch(SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch(Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(countriesDTO, MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
	@RequestMapping(value="/getAllTitles", method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON)
	public Response getAllTitles() {
		SimpleResponseDTO response = new SimpleResponseDTO();
		List<String> titles = new ArrayList<>();
		try {
			
			titles = siteService.getAllTitles();
		
		} catch(SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch(Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(titles, MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
	@RequestMapping(value="/filter", method = RequestMethod.POST, 
			produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	public Response filter(@RequestBody FilterSiteIDTO filterSiteIDTO) {
		SimpleResponseDTO response = new SimpleResponseDTO();
		SitesODTO sites = null;
		try {
			
			sites = siteService.filterSites(filterSiteIDTO);
		
		} catch(SimpleException se) {
			response.setStatus(se.getSimpleResponseStatus());
		} catch(Exception e) {
			e.printStackTrace();
			response.setStatus(SimpleResponseDTO.GENERAL_SERVER_ERROR);
		}
		if (response.getStatus() == SimpleResponseDTO.OK) {
			return Response.ok(sites, MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	
}
