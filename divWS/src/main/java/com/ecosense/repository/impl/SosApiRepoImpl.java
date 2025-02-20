package com.ecosense.repository.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ecosense.dto.output.MeasurementODTO;
import com.ecosense.dto.output.MeasurementsODTO;
import com.ecosense.dto.output.PhenomenonODTO;
import com.ecosense.entity.Phenomenon;
import com.ecosense.entity.Procedure;
import com.ecosense.entity.SosPath;
import com.ecosense.entity.Station;
import com.ecosense.entity.TimeSeries;
import com.ecosense.repository.SosApiRepo;
import com.ecosense.utils.SOSData;
import com.ecosense.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

@Repository
public class SosApiRepoImpl implements SosApiRepo {
	

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public List<Phenomenon> getPhenomenons(String url) {
		List<Phenomenon> phenomenons = new ArrayList<>();
		JsonNode phenomenonsNode = null;
		
		try {
			ResponseEntity<JsonNode> phenomenonResponse = restTemplate.exchange(url + SOSData.PHENOMENON_SUFFIX, HttpMethod.GET, null, JsonNode.class);
			phenomenonsNode = phenomenonResponse.getBody();
			
		} catch (Exception e) {
			System.out.println(url + SOSData.PHENOMENON_SUFFIX + " <- SERVIS KOJI PUKNE");
			return phenomenons;
		}
		
		if (phenomenonsNode != null) {
			for (JsonNode phenomenonNode :phenomenonsNode) {
				Phenomenon phenomenon = new Phenomenon();
				phenomenon.setSosId(phenomenonNode.get("id").asInt());
				phenomenon.setLabel(phenomenonNode.get("label").asText());
				phenomenon.setDomainid(phenomenonNode.get("domainId").asText());
				String phenomenonEn = translatePhenomenon(phenomenon.getDomainid());
				phenomenon.setLabelEn(phenomenonEn != null ? phenomenonEn : phenomenon.getLabel());
				phenomenons.add(phenomenon);
			}
		}
			
		return phenomenons;
	}
	
	
	@Override
	public String translatePhenomenon(String domainId) {
		String uri = URLDecoder.decode(domainId, StandardCharsets.UTF_8);
		
		int uriFirstIndex = uri.indexOf("http");
		int uriLastIndex = uri.lastIndexOf("http");
		
		if (uriFirstIndex != -1 && uriFirstIndex < uriLastIndex) {
			uri = uri.substring(uriLastIndex);
		}
		

		int uriLastIndexHash = uri.lastIndexOf("#");
		if (uriLastIndexHash > -1) {
			uri = uri.substring(0, uriLastIndexHash);
		}
		
		String url = SOSData.ENVTHES_REST_API + uri + SOSData.ENVTHES_FORMAT_SUFFIX;
		JsonNode responseNode = null;
		
		try {
			ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, null, JsonNode.class);
			responseNode = response.getBody();
		} catch (Exception e) {
			System.out.println(url + " <- SERVIS KOJI PUKNE");
			return null;
		}
		
		JsonNode graphs = responseNode.get("graph");
		
		if(graphs.isArray()) {
			for(JsonNode graph : graphs) {
				
				if (graph.get("type").asText().equals("skos:Concept") && graph.get("uri").asText().equals(uri)) {
				JsonNode prefLabelNode =  graph.get("prefLabel");
					if (prefLabelNode != null) {
						if (prefLabelNode.isArray()) {
							for(JsonNode prefLabel : prefLabelNode) {
								if (prefLabel.get("lang").asText().equals("en")) {
									String transalted = prefLabel.get("value").asText();
									return transalted;
								}
							}
						} else if (prefLabelNode != null) {
							String transalted = prefLabelNode.get("value").asText();
							return transalted;
						}
					}
				}
			}
			
		}
		return null;
	}
	
	@Override
	public List<Procedure> getProcedures(String url) {
		List<Procedure> procedures = new ArrayList<>();
		
		try {
			ResponseEntity<JsonNode> proceduresResponse = restTemplate.exchange(url + SOSData.PROCEDURES_SUFFIX, HttpMethod.GET, null, JsonNode.class);
			JsonNode proceduresNode = proceduresResponse.getBody();
	
			for (JsonNode procedureNode : proceduresNode) {
				Procedure procedure = new Procedure();
				procedure.setSosId(procedureNode.get("id").asInt());
				procedure.setLabel(procedureNode.get("label").asText());
				procedures.add(procedure);
			}
			
		} catch (Exception e) {
			System.out.println(url + SOSData.PROCEDURES_SUFFIX + " <- SERVIS KOJI PUKNE");
			return procedures;
		}
			
		return procedures;
	}
			
	@Override
	public MeasurementsODTO getMeasurements(TimeSeries timeSeries, Date dateFrom, Date dateTo) {
		MeasurementsODTO measResponseDTO = new MeasurementsODTO();
		List<MeasurementODTO> measurementsDTO = new ArrayList<>(); 
		
		if (dateFrom == null && dateTo == null) {
			dateTo = timeSeries.getLastTime();
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateTo);
			cal.add(Calendar.DAY_OF_MONTH, -10);
			dateFrom = cal.getTime();
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String dateFromFormatted = sdf.format(dateFrom);
		String dateToFormatted = sdf.format(dateTo);
		
		String urlRequest = timeSeries.getStation().getSosPath().getUrl() + SOSData.TIMESERIES_SUFFIX + timeSeries.getSosId() + "/getData?expanded=true&format=flot&generalize=true&locale=de&timespan=" + dateFromFormatted + "/" + dateToFormatted;
		
		ResponseEntity<JsonNode> timeSeriesResponse = restTemplate
				.exchange(urlRequest, HttpMethod.GET, null, JsonNode.class);
		JsonNode bodyNode = timeSeriesResponse.getBody(); 
		JsonNode timeseriesNode = bodyNode.get(timeSeries.getSosId().toString());
		JsonNode valuesNode = timeseriesNode.get("values");
		
		PhenomenonODTO phenomDTO = new PhenomenonODTO();
		phenomDTO.setLabel(timeSeries.getPhenomenon().getLabelEn());
		phenomDTO.setId(timeSeries.getPhenomenon().getId());
		measResponseDTO.setPhenomenon(phenomDTO);
		measResponseDTO.setUom(timeSeries.getUom());
		measResponseDTO.setStation(timeSeries.getStation().getTitle());
		measResponseDTO.setProcedure(timeSeries.getProcedure().getLabel());
		measResponseDTO.setTimeseriesId(timeSeries.getId());
		
		if (valuesNode.isArray()) {
			for (JsonNode value : valuesNode) {
				Date date = new Date(Long.parseLong(value.get(0).toString()));
				Double measValue = Utils.roundToNumOfDecimals(value.get(1).asDouble(), 2);
				
				MeasurementODTO measDTO = new MeasurementODTO();
				measDTO.setDate(date);
				measDTO.setValue(measValue);
				
				measurementsDTO.add(measDTO);
			}
		}
		
		measResponseDTO.setMeasurements(measurementsDTO);
		return measResponseDTO;
	}
	
	@Override
	public List<TimeSeries> getTimeSeries(String url) {
		List<TimeSeries> timeSeriesList = new ArrayList<>();
		JsonNode timeSeriesListNode = null;
		
		try {
			ResponseEntity<JsonNode> timeSeriesListResponse = restTemplate.exchange(url + SOSData.TIMESERIES_SUFFIX, HttpMethod.GET, null, JsonNode.class);
			timeSeriesListNode = timeSeriesListResponse.getBody();		
		} catch (Exception e) {
			System.out.println(url + SOSData.TIMESERIES_SUFFIX + " <- SERVIS KOJI PUKNE");
			return timeSeriesList;
		}
	
		for(JsonNode tsNode : timeSeriesListNode) {
			TimeSeries timeSeries = new TimeSeries();
			timeSeries.setSosId(tsNode.get("id").asInt());
			timeSeries.setLabel(tsNode.get("label").asText());
			timeSeries.setUom(tsNode.get("uom").asText());
			timeSeries.setStation(new Station(tsNode.get("station").get("id").asInt()));
			
			ResponseEntity<JsonNode> timeSeriesResponse = restTemplate.exchange(url + SOSData.TIMESERIES_SUFFIX + timeSeries.getSosId(), HttpMethod.GET, null, JsonNode.class);
			JsonNode timeSeriesNode = timeSeriesResponse.getBody();
			
			JsonNode lastValueNode = timeSeriesNode.get("lastValue");
			Double value = lastValueNode.get("value").asDouble();
			Date date = new Date(lastValueNode.get("timestamp").asLong());
				
			timeSeries.setLastTime(date);
			timeSeries.setLastValue(value);
			
			JsonNode firstValueNode = timeSeriesNode.get("firstValue");
			Double valueFirst = firstValueNode.get("value").asDouble();
			Date dateFirst = new Date(firstValueNode.get("timestamp").asLong());
				
			timeSeries.setFirstTime(dateFirst);
			timeSeries.setFirstValue(valueFirst);
			
			Procedure procedure = new Procedure(timeSeriesNode.get("parameters").get("procedure").get("id").asInt());
			procedure.setLabel(timeSeriesNode.get("parameters").get("procedure").get("label").asText());
			timeSeries.setProcedure(procedure);
			
			Phenomenon phenomenon = new Phenomenon(timeSeriesNode.get("parameters").get("phenomenon").get("id").asInt());
			phenomenon.setLabel(timeSeriesNode.get("parameters").get("phenomenon").get("label").asText());
			timeSeries.setPhenomenon(phenomenon);
			
			if (timeSeries.getSosId().equals(2)) {
				System.out.println("sos je 2 vrednost " + timeSeries.getFirstValue());
			}
			
			timeSeriesList.add(timeSeries);
		}
		
		return timeSeriesList;
	}

	@Override
	public Boolean isProviderValid(String provider) {
		try {
			System.out.println(provider + SOSData.STATIONS_SUFFIX);
			ResponseEntity<JsonNode> timeSeriesListResponse = restTemplate.exchange(provider + SOSData.STATIONS_SUFFIX, HttpMethod.GET, null, JsonNode.class);
			System.out.println(provider + SOSData.STATIONS_SUFFIX);
			JsonNode timeSeriesListNode = timeSeriesListResponse.getBody();
			if (!timeSeriesListNode.isArray()) {
				return false;
			}
			return true;
		} catch (RestClientException e) {
			return false;
		}
	}

}
