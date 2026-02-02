package com.ecosense.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.DivFilterDTO;
import com.ecosense.dto.PointDTO;
import com.ecosense.dto.PolygonDTO;
import com.ecosense.dto.SimpleResponseDTO;
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
import com.ecosense.utils.APICallService;
import com.ecosense.utils.SolrData;
import com.ecosense.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

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
	private APICallService apiCallService;

	private static final Logger log = LoggerFactory.getLogger(SiteServiceImpl.class);
	
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
		site.setLastChecked(new Date());

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
			
			sitesDTO.add(siteDTO);
		}
		Collections.sort(sitesDTO);
		
		return resultODTO;
	}
	
	@Override
	@Transactional
	public void refreshDatabase() throws SimpleException {
		List<Site> sites = deimsRepo.getAllSites();
		for (Site siteFromApi : sites) {
			try {
				saveSite(siteFromApi);
			} catch (Exception e) {
				log.error("Error while processing site: " + siteFromApi.getTitle());
				e.printStackTrace();
			}

		}
		try {
			deleteAllInactiveSites();
		} catch (Exception e) {
			log.error("Error while deleting inactive sites");
			e.printStackTrace();
		}
	}

	private void deleteAllInactiveSites() {
        List<Site> allSites = siteJpaRepo.findAll();
        for (Site site : allSites) {
            if (checkIfSiteIsForDeletion(site)) {
                deleteOneSite(site);
            }
        }
    }

    private Boolean checkIfSiteIsForDeletion(Site site) {
        return site.getLastChecked() == null || Utils.isOlderThanOneDay(site.getLastChecked());
    }

	private void deleteOneSite(Site site) {
		siteJpaRepo.delete(site);

		log.info("Deleted inactive site: " + site.getTitle());
	}


	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void saveSite(Site siteFromApi) throws SimpleException {
		log.info("Processing site: " + siteFromApi.getTitle());
		Site siteFromDB = siteRepo.getSite(siteFromApi.getIdSuffix());

		if (siteFromDB == null) {
			log.info("New site found: " + siteFromApi.getTitle());
			insertNewSite(siteFromApi);
		} else {
			siteFromDB.setLastChecked(new Date());
			siteRepo.updateSite(siteFromDB);
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

		BoundingBoxDTO boundingBox = new BoundingBoxDTO(Double.MAX_VALUE, Double.MAX_VALUE, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		
		List<Object[]> sitesFromDB = siteRepo.filterSites(filterSiteIDTO);
		List<SiteODTO> sites = new ArrayList<>();
		
		
		for (Object[] site : sitesFromDB) {
			SiteODTO siteDTO = new SiteODTO();
			siteDTO.setId((Integer) site[0]);
			
			Point point  = (Point) site[1];
			double lng = point.getCoordinate().x;
			double lat = point.getCoordinate().y;
			siteDTO.setPoint(new PointDTO(lat, lng));
			
			if (site[2] == null) {
				continue;
			}
			Geometry polygon  = (Geometry) site[2];

			StringWriter writer = new StringWriter();
			GeoJsonWriter geoJsonWriter = new GeoJsonWriter();
			geoJsonWriter.write(polygon, writer);
			
			double[] minMaxPoints = Utils.transformEpsg(polygon.getEnvelopeInternal().getMinX(), polygon.getEnvelopeInternal().getMinY(), 
														polygon.getEnvelopeInternal().getMaxX(), polygon.getEnvelopeInternal().getMaxY(), 
														"EPSG:4326", "EPSG:3857");
			if (minMaxPoints == null) {
				continue;
			}
			BoundingBoxDTO siteBbox = new BoundingBoxDTO(minMaxPoints[0], minMaxPoints[1], minMaxPoints[2], minMaxPoints[3]);
			
			boundingBox = Utils.setBB(siteBbox, boundingBox);

			sites.add(siteDTO);
		}

		response.setSites(sites);
		response.setBoundingBox(boundingBox);
		return response;
	}

	@Override
	public String getGeoJsonPolygon(Integer id) {
		Site site = siteJpaRepo.getOne(id);
		
		GeoJsonWriter writer = new GeoJsonWriter();
		if (site.getPolygon() == null || !(site.getPolygon() instanceof Polygon || site.getPolygon() instanceof MultiPolygon)) {
			return writer.write(site.getCircle());
		} else {
			return writer.write(site.getPolygon());
		}
	}

	public SitesODTO filterAndSearchSites(DivFilterDTO divFilterDTO) throws SimpleException, IOException {
		SitesODTO response = new SitesODTO();
		Set<SiteODTO> sites = new HashSet<>();
		BoundingBoxDTO boundingBox = new BoundingBoxDTO(Double.MAX_VALUE, Double.MAX_VALUE, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

		String urlData = SolrData.BASE_URL + "/select?q=data:(";
		String urlJsonDataset = SolrData.BASE_URL + "/select?q=json_dataset:(";

		String filterUrl = createUrlFromDivFilter(divFilterDTO);
		urlData += filterUrl;
		urlJsonDataset += filterUrl;

		urlData += ")&fl=id";
		urlData += "&rows=1000&start=0";

		urlJsonDataset += ")&fl=site_uuid";
		urlJsonDataset += "&rows=1000&start=0";

		JsonNode resultNodeData = apiCallService.getRequest(urlData);
		JsonNode resultNodeJsonDataset = apiCallService.getRequest(urlJsonDataset);

		JsonNode siteIdDataListNode = resultNodeData.get("response").get("docs");
		JsonNode siteIdJsonDatasetListNode = resultNodeJsonDataset.get("response").get("docs");

		ArrayNode combinedNodes = JsonNodeFactory.instance.arrayNode();
		if (siteIdDataListNode.isArray()) {
			combinedNodes.addAll((ArrayNode) siteIdDataListNode);
		}
		if (siteIdJsonDatasetListNode.isArray()) {
			combinedNodes.addAll((ArrayNode) siteIdJsonDatasetListNode);
		}

		for (JsonNode siteIdNode : combinedNodes) {
			JsonNode siteSuffixNode = siteIdNode.get("id");
			if (siteSuffixNode == null) {
				siteSuffixNode = siteIdNode.get("site_uuid");
			}

			String siteSuffix = siteSuffixNode.asText();

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

				Boolean isPolygon = site.getPolygon() != null && (site.getPolygon() instanceof Polygon || site.getPolygon() instanceof MultiPolygon);
				Geometry geom = null;
				if (isPolygon) {
					geom = site.getPolygon();
				} else {
					geom = site.getCircle();
				}

				if (geom != null) {
					siteDTO.setArea(geom.getArea());
					
					StringWriter writer = new StringWriter();
					GeoJsonWriter geoJsonWriter = new GeoJsonWriter();
					geoJsonWriter.write(geom, writer);
					String polygonJson = writer.toString();

					double[] minMaxPoints = Utils.transformEpsg(geom.getEnvelopeInternal().getMinX(), geom.getEnvelopeInternal().getMinY(), 
															geom.getEnvelopeInternal().getMaxX(), geom.getEnvelopeInternal().getMaxY(), 
															"EPSG:4326", "EPSG:3857");

					if (minMaxPoints == null) {
						continue;
					}

					BoundingBoxDTO siteBbox = new BoundingBoxDTO(minMaxPoints[0], minMaxPoints[1], minMaxPoints[2], minMaxPoints[3]);
					
					siteDTO.setPolygon(new PolygonDTO(polygonJson, siteBbox, isPolygon));

					boundingBox = Utils.setBB(siteBbox, boundingBox);
				}
				

				sites.add(siteDTO);
			}
		}

		response.setSites(new ArrayList<>(sites));
		response.setBoundingBox(boundingBox);

        return response;
	}

	public String createUrlFromDivFilter(DivFilterDTO divFilterDTO) {
		String url = "";
		List<String> searchParameters = new ArrayList<>();

		List<String> siteSearchParameters = new ArrayList<>(); // for keyword site

		if (divFilterDTO.getSiteIds() != null && !divFilterDTO.getSiteIds().isEmpty()) {
			for (Integer siteId : divFilterDTO.getSiteIds()) {
				SiteDetailsODTO siteODTO = null;
				try {
					siteODTO = getSiteInfo(siteId);
				} catch (Exception e) { }
				if (siteODTO != null && siteODTO.getTitle() != null) {
					searchParameters.add(siteODTO.getTitle());
				}; 
			}
		}


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

		if (searchParameters.isEmpty() && siteSearchParameters.isEmpty()) {
			return null;
		}

		int i = 0;
		for (String param : searchParameters) {
			if (i != 0) {
				url += " OR ";
			}
			url += '"' + param + '"';

			i++;
		}

		return url;
	}

	@Override
	public SiteODTO getSite(Integer siteId) {
		Site site = siteRepo.getSite(siteId);

		SiteODTO siteODTO = new SiteODTO();
		siteODTO.setId(site.getId());
		siteODTO.setIdSuffix(site.getIdSuffix());
		siteODTO.setTitle(site.getTitle());

		return siteODTO;
	}

}
