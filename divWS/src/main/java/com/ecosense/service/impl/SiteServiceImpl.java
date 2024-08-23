package com.ecosense.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.DivFilterDTO;
import com.ecosense.dto.PointDTO;
import com.ecosense.dto.PolygonDTO;
import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.StandardObservationDTO;
import com.ecosense.dto.input.FilterSiteIDTO;
import com.ecosense.dto.output.ActivityODTO;
import com.ecosense.dto.output.CountryODTO;
import com.ecosense.dto.output.SiteDetailsODTO;
import com.ecosense.dto.output.SiteODTO;
import com.ecosense.dto.output.SitesODTO;
import com.ecosense.entity.Activity;
import com.ecosense.entity.Country;
import com.ecosense.entity.Site;
import com.ecosense.exception.SimpleException;
import com.ecosense.repository.ActivityRepo;
import com.ecosense.repository.CountryRepo;
import com.ecosense.repository.DeimsApiRepository;
import com.ecosense.repository.SiteJpaRepo;
import com.ecosense.repository.SiteRepo;
import com.ecosense.service.SiteService;
import com.ecosense.utils.SolrData;
import com.ecosense.utils.Utils;
import com.ecosense.utils.VocabsData;
import com.fasterxml.jackson.databind.JsonNode;

@Service
@Transactional
public class SiteServiceImpl implements SiteService {

	@Autowired
	private SiteRepo siteRepo;
	
	@Autowired
	private SiteJpaRepo siteJpaRepo;
	
	@Autowired
	private DeimsApiRepository deimsRepo;
	
	@Autowired
	private ActivityRepo activityRepo;
	
	@Autowired
	private CountryRepo countryRepo;

	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public SiteDetailsODTO getSiteInfo(Integer siteId) throws SimpleException, SQLException, IOException {
		if (siteId == null) {
			throw new SimpleException(SimpleResponseDTO.DATA_NOT_COMPLETE);
		}
		
		Site site  = siteJpaRepo.getOne(siteId);
		return deimsRepo.getSiteInfo(site.getIdSuffix());
	}
	
	private void insertNewSite(Site siteFromApi) throws SimpleException {
		Site site =  deimsRepo.getPolygon(siteFromApi.getIdSuffix());
		site.setIdSuffix(siteFromApi.getIdSuffix());
		site.setChanged(siteFromApi.getChanged());
		site.setTitle(siteFromApi.getTitle());
		site.setPoint(siteFromApi.getPoint());
		
		List<Activity> activities = deimsRepo.getActivities(siteFromApi.getIdSuffix());
		for (Activity act : activities) {
			Activity actFromDB = activityRepo.findActivity(act.getActivityName());
			if (actFromDB == null) {
				actFromDB = activityRepo.insertActivity(act);
			}
			site.getActivities().add(actFromDB);
		}
		
		List<Country> countries = deimsRepo.getCountries(siteFromApi.getIdSuffix());
		for (Country country : countries) {
			Country countryFromDB = countryRepo.findCountry(country.getCountryName());
			if (countryFromDB == null) {
				countryFromDB = countryRepo.insertCountry(country);
			}
			site.getCountries().add(countryFromDB);
		}
		
		siteRepo.insertSite(site);
		
		updateCircleOfSite(site);
	}
	
	private void updateCircleOfSite(Site site) {
		if ((site.getPolygon() == null || (site.getPolygon() instanceof Point)))  {
			Double areaHa = site.getArea();
			if (site.getArea() == null || site.getArea() == 0) {
				areaHa = 0.01;
			}
			Double radius = Math.sqrt(areaHa / Math.PI);
			
			siteRepo.createCircle(site, radius);
		}
	}

	@Override
	public void addAllToDB() throws SimpleException {
		List<Site> sites = deimsRepo.getAllSites();

		for (Site site : sites) {
			insertNewSite(site);
		}
	}
	
	@Override
	public SitesODTO getAllPolygons() throws SimpleException, IOException {
		SitesODTO resultODTO = new SitesODTO();
		List<Site> sites = siteRepo.getAllPolygons();
		List<SiteODTO> sitesDTO = new ArrayList<>();
		
		BoundingBoxDTO boundingBox = new BoundingBoxDTO(Double.MAX_VALUE, Double.MAX_VALUE, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		
		for (Site site : sites) {
			SiteODTO siteDTO = new SiteODTO();
			siteDTO.setId(site.getId());
			siteDTO.setIdSuffix(site.getIdSuffix());
			siteDTO.setTitle(site.getTitle());
			
			Point point  = (Point) site.getPoint();
			double lng = point.getCoordinate().x;
			double lat = point.getCoordinate().y;
			siteDTO.setPoint(new PointDTO(lat, lng));
			
			
			Geometry polygon = site.getPolygon();
			Boolean isPoygon = true;
			
			siteDTO.setArea(polygon.getArea());
			
			StringWriter writer = new StringWriter();
			GeoJsonWriter geoJsonWriter = new GeoJsonWriter();
			geoJsonWriter.write(polygon, writer);
			String polygonJson = writer.toString();
			
			BoundingBoxDTO siteBbox = new BoundingBoxDTO(polygon.getEnvelopeInternal().getMinX(), polygon.getEnvelopeInternal().getMinY(),
					polygon.getEnvelopeInternal().getMaxX(), polygon.getEnvelopeInternal().getMaxY());
			
			siteDTO.setPolygon(new PolygonDTO(polygonJson, siteBbox, isPoygon));
			
			boundingBox = Utils.setBB(siteBbox, boundingBox);
			
			sitesDTO.add(siteDTO);
		}
		Collections.sort(sitesDTO);
		
		resultODTO.setBoundingBox(boundingBox);
		return resultODTO;
	}
	
	@Override
	public void refreshDatabase() throws SimpleException {
		System.out.println("Start of site refresh");
		List<Site> sites = deimsRepo.getAllSites();
		for (Site siteFromApi : sites) {
			Site siteFromDB = siteRepo.getSite(siteFromApi.getIdSuffix());

			if (siteFromDB == null) {
				insertNewSite(siteFromApi);
			} else {
				if (siteFromApi.getChanged().compareTo(siteFromDB.getChanged()) != 0) {
					Site sitePolygon = deimsRepo.getPolygon(siteFromApi.getIdSuffix());
					siteFromDB.setArea(sitePolygon.getArea());
					siteFromDB.setPolygon(sitePolygon.getPolygon());
					siteFromDB.setChanged(siteFromApi.getChanged());
					siteFromDB.setTitle(siteFromApi.getTitle());
					siteFromDB.setPoint(siteFromApi.getPoint());
					
					// refresh activities
					List<Activity> activities = deimsRepo.getActivities(siteFromApi.getIdSuffix());
					for (Activity act : activities) {
						Activity activityFromDB = activityRepo.findActivity(act.getActivityName());
						if (!siteFromDB.getActivities().contains(act)) {
							if (activityFromDB == null) {
								activityFromDB = activityRepo.insertActivity(act);
							}
							siteFromDB.getActivities().add(activityFromDB);
						}
					}
					// if country is deleted
					Iterator<Activity> actIterator = siteFromDB.getActivities().iterator();
					while (actIterator.hasNext()) {
						if (!activities.contains(actIterator.next())) {
							actIterator.remove();
						}
					}
					
					// refresh countries
					List<Country> countries = deimsRepo.getCountries(siteFromApi.getIdSuffix());
					for (Country country : countries) {
						Country countryFromDB = countryRepo.findCountry(country.getCountryName());
						if (!siteFromDB.getCountries().contains(country)) {
							if (countryFromDB == null) {
								countryFromDB = countryRepo.insertCountry(country);
							}
							siteFromDB.getCountries().add(countryFromDB);
						}
					}
					// if country is deleted
					Iterator<Country> countryIterator = siteFromDB.getCountries().iterator();
					while (countryIterator.hasNext()) {
						if (!countries.contains(countryIterator.next())) {
							countryIterator.remove();
						}
					}
					
					siteRepo.updateSite(siteFromDB);
					
					updateCircleOfSite(siteFromDB);
				}
			}
		}

		System.out.println("End of site refresh");
	}
	
	@Override
	public List<SiteODTO> getAllPoints() throws SimpleException {
		List<Site> sites = siteRepo.getAllPoints();
		List<SiteODTO> sitesDTO = new ArrayList<>();
		
		for (Site site : sites) {
			SiteODTO siteDTO = new SiteODTO();
			siteDTO.setId(site.getId());
			siteDTO.setIdSuffix(site.getIdSuffix());
			siteDTO.setTitle(site.getTitle());
			
			Point point  = (Point) site.getPoint();
			double lng = point.getCoordinate().x;
			double lat = point.getCoordinate().y;
			siteDTO.setPoint(new PointDTO(lat, lng));
			
			sitesDTO.add(siteDTO);
		}
		
		return sitesDTO;
	}
	
	@Override
	public List<ActivityODTO> getAllActivities() throws SimpleException {
		List<Activity> activitiesFromDB = activityRepo.getAllActivities();
		List<ActivityODTO> activityODTOs = new ArrayList<>();
		
		for (Activity activity : activitiesFromDB) {
			activityODTOs.add(new ActivityODTO(activity.getId(), activity.getActivityName()));
		}
		
		return activityODTOs;
	}

	@Override
	public List<CountryODTO> getAllCountries() throws SimpleException {
		List<Country> countriesFromDB = countryRepo.getAllCountries();
		List<CountryODTO> countryODTOs = new ArrayList<>();
		
		for (Country country : countriesFromDB) {
			countryODTOs.add(new CountryODTO(country.getId(), country.getCountryName()));
		}
		
		return countryODTOs;
	}

	@Override
	public List<String> getAllTitles() throws SimpleException {
		List<String> titles = siteRepo.getAllTitles();
		return titles;
	}

	@Override
	public SitesODTO filterSites(FilterSiteIDTO filterSiteIDTO) throws SimpleException, IOException {
		SitesODTO response = new SitesODTO();
		
		List<Object[]> sitesFromDB = siteRepo.filterSites(filterSiteIDTO);
		
		List<SiteODTO> sites = new ArrayList<>();
		
		
		for (Object[] site : sitesFromDB) {
			SiteODTO siteDTO = new SiteODTO();
			siteDTO.setId((Integer) site[0]);
			
			Point point  = (Point) site[1];
			double lng = point.getCoordinate().x;
			double lat = point.getCoordinate().y;
			siteDTO.setPoint(new PointDTO(lat, lng));
			
			sites.add(siteDTO);
		}

		response.setSites(sites);
		return response;
	}

	@Override
	public String getGeoJsonPolygon(Integer id) {
		Site site = siteJpaRepo.getOne(id);
		
		GeoJsonWriter writer = new GeoJsonWriter();
		return writer.write(site.getPolygon());
	}

	public SitesODTO filterAndSearchSites(DivFilterDTO divFilterDTO) throws SimpleException, IOException {
		SitesODTO response = new SitesODTO();
		List<SiteODTO> sites = new ArrayList<>();
		BoundingBoxDTO boundingBox = new BoundingBoxDTO(Double.MAX_VALUE, Double.MAX_VALUE, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

		JsonNode resultNode = null;

		String url = SolrData.BASE_URL + "/select?q=data:(";

		List<String> searchParameters = new ArrayList<>();
		if (divFilterDTO.getHabitats() != null && !divFilterDTO.getHabitats().isEmpty()) {
			searchParameters.addAll(divFilterDTO.getHabitats());
		}
		if (divFilterDTO.getSites() != null && !divFilterDTO.getSites().isEmpty()) {
			searchParameters.addAll(divFilterDTO.getSites());
		}
		if (divFilterDTO.getStandardObservations() != null && !divFilterDTO.getStandardObservations().isEmpty()) {
			searchParameters.addAll(divFilterDTO.getStandardObservations());
		}
		if (divFilterDTO.getSearchText() != null && !divFilterDTO.getSearchText().isEmpty()) {
			searchParameters.addAll(Arrays.asList(divFilterDTO.getSearchText()));
		}

		int i = 0;
		for (String param : searchParameters) {
			if (i != 0) {
				url += " OR ";
			}
			url += '"' + param + '"';

			i++;
		}

		url += ")&fl=id";

        try {
			ResponseEntity<JsonNode> siteResponse = restTemplate.exchange(url, HttpMethod.GET, null, JsonNode.class);
			resultNode = siteResponse.getBody();

		} catch (Exception e) {
			System.out.println(url + " <- SERVIS KOJI PUKNE");
			return response;
		}

		JsonNode siteIdListNode = resultNode.get("response").get("docs");

		for (JsonNode siteIdNode : siteIdListNode) {
			String siteSuffix = siteIdNode.get("id").asText();

			Site site = siteRepo.getSite(siteSuffix);

			if (site != null) {

				SiteODTO siteDTO = new SiteODTO();
				siteDTO.setId(site.getId());
				siteDTO.setIdSuffix(site.getIdSuffix());
				siteDTO.setTitle(site.getTitle());

				Point point  = (Point) site.getPoint();
				double lng = point.getCoordinate().x;
				double lat = point.getCoordinate().y;
				siteDTO.setPoint(new PointDTO(lat, lng));

				Geometry polygon = site.getPolygon();
				Boolean isPoygon = true;

				siteDTO.setArea(polygon.getArea());
				
				StringWriter writer = new StringWriter();
				GeoJsonWriter geoJsonWriter = new GeoJsonWriter();
				geoJsonWriter.write(polygon, writer);
				String polygonJson = writer.toString();
				
				BoundingBoxDTO siteBbox = new BoundingBoxDTO(polygon.getEnvelopeInternal().getMinX(), polygon.getEnvelopeInternal().getMinY(),
						polygon.getEnvelopeInternal().getMaxX(), polygon.getEnvelopeInternal().getMaxY());
				
				siteDTO.setPolygon(new PolygonDTO(polygonJson, siteBbox, isPoygon));
				
				boundingBox = Utils.setBB(siteBbox, boundingBox);

				sites.add(siteDTO);
			}
		}

		response.setSites(sites);
		response.setBoundingBox(boundingBox);

        return response;
	}
	
}
