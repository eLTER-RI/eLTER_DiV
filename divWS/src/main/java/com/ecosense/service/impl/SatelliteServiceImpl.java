package com.ecosense.service.impl;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.input.FilterSatelliteIDTO;
import com.ecosense.dto.output.SatelliteODTO;
import com.ecosense.entity.Site;
import com.ecosense.exception.SimpleException;
import com.ecosense.repository.SiteJpaRepo;
import com.ecosense.repository.SiteRepo;
import com.ecosense.service.SatelliteService;
import com.ecosense.utils.SatelliteData;
import com.ecosense.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class SatelliteServiceImpl implements SatelliteService {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private SiteRepo siteRepo;
	
	@Autowired
	private SiteJpaRepo siteJpaRepo;

	/**
	 * By given date range, bbox and layer types list (all are optional) returns a list of satellite images 
	 * from GeoServer (use satellite API as intermediate layer). 
	 */
	@Override
	public List<SatelliteODTO> getMapList(FilterSatelliteIDTO filterSatelliteIDTO, Integer siteId) throws SimpleException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("apiToken", SatelliteData.SATELLITE_TOKEN);
		
		BoundingBoxDTO bbox = siteRepo.getBoundingBox4326(siteId);
		filterSatelliteIDTO.setPreviewBox(bbox);
		filterSatelliteIDTO.setSrid(4326);
		
		HttpEntity<FilterSatelliteIDTO> request = new HttpEntity<FilterSatelliteIDTO>(filterSatelliteIDTO, headers);
		ResponseEntity<JsonNode> response = restTemplate.exchange(SatelliteData.SATELLITE_FILTER_URL, HttpMethod.POST, request, JsonNode.class);
		
		JsonNode satellitesRes = response.getBody();
		
		List<SatelliteODTO> res = new ArrayList<>();;
				
		for(JsonNode satelliteRes : satellitesRes) {
			SatelliteODTO satellite = new SatelliteODTO();
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
			try {
				Date mapDate = sdf.parse(satelliteRes.get("date").asText());
				satellite.setDate(mapDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			satellite.setPkLayer(satelliteRes.get("pkLayer").asInt());
		
			String satImage = satelliteRes.get("satImage").get("imageRequestUrl").asText().replace("SatelliteWS", "satellite");
			satImage = satImage.replaceAll("https://agrosens.rs/satellite2/image/one", "https://ecosense.biosense.rs/ecosense/rest/satellite/image/one");
//			satImage = satImage.replaceAll("https://agrosens.rs/satellite2/image/one", "http://localhost:8080/ecosense/rest/satellite/image/one");
			satellite.setImageRequestUrl(satImage+ "&siteId="+siteId);
			satellite.setLayerType(satelliteRes.get("layerType").get("name").asText());
			
			res.add(satellite);
		}
		
		return res;
	}


	@Override
	public byte[] getImage(Integer siteId, Integer pkLayer, Boolean keepAspectRatio, Double maxX, Double maxY, Double minX, Double minY) throws SimpleException {
		StringBuilder serviceUrl = new StringBuilder("?pkLayer=" + pkLayer);
		
		Site site =  siteJpaRepo.getOne(siteId);
		Geometry polygonOfSite = site.getPolygon(); 
		
		GeometryFactory gf = new GeometryFactory();
		ArrayList<Geometry> geometryPoints = new ArrayList<>();
		geometryPoints.add(gf.createPoint(new Coordinate(maxX, maxY))); 
		geometryPoints.add(gf.createPoint(new Coordinate(maxX, minY)));
		geometryPoints.add(gf.createPoint(new Coordinate(minX, minY)));
		geometryPoints.add(gf.createPoint(new Coordinate(minX, maxY)));
		geometryPoints.add(gf.createPoint(new Coordinate(maxX, maxY)));
		
		Coordinate[] cooredinate = new Coordinate[geometryPoints.size()];
		for (int i = 0; i < geometryPoints.size(); i++) {
			cooredinate[i] = geometryPoints.get(i).getCoordinate();
		}
		
		Geometry polygonOfBBox = gf.createPolygon(cooredinate);
		
		if (!polygonOfSite.intersects(polygonOfBBox)) {
			throw new SimpleException(SimpleResponseDTO.NOT_ALLOWED);
		}
		
		
		String bboxUrlPart = "&minx=" + minX + "&maxx=" + maxX + "&miny=" + minY + "&maxy=" + maxY;
		
		serviceUrl.append(bboxUrlPart);
		if (keepAspectRatio != null) {
			serviceUrl.append("&keepAspectRatio=" + keepAspectRatio);
		}
		serviceUrl.append("&srid=" + 4326);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("apiToken", SatelliteData.SATELLITE_TOKEN);
		HttpEntity entity = new HttpEntity(headers);
		
		ResponseEntity<byte[]> response = restTemplate.exchange(SatelliteData.SATELLITE_SINGLE_URL + serviceUrl.toString(), HttpMethod.GET, entity, byte[].class);
		
		return response.getBody();
	}
	
}
