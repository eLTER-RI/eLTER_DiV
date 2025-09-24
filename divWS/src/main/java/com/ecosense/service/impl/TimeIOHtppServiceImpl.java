package com.ecosense.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecosense.dto.PointDTO;
import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.timeio.TimeIODatastreamDTO;
import com.ecosense.dto.timeio.TimeIOLocationDTO;
import com.ecosense.dto.timeio.TimeIOObservationDTO;
import com.ecosense.dto.timeio.TimeIOThingDTO;
import com.ecosense.entity.TimeIODatastream;
import com.ecosense.exception.SimpleException;
import com.ecosense.service.TimeIOHttpService;
import com.ecosense.utils.TimeIOData;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class TimeIOHtppServiceImpl implements TimeIOHttpService {

    @Autowired private RestTemplate restTemplate;

    @Override
    public List<TimeIOLocationDTO> getAllLocations() throws SimpleException {
        ResponseEntity<JsonNode> locationsResponse = null;

        String url = TimeIOData.TIMEIO_BASE_URL + TimeIOData.TIMEIO_LOCATION_SUFFIX;

		try {
			locationsResponse = restTemplate.exchange(url, 
				                                      HttpMethod.GET, 
                                                      null, 
                                                      JsonNode.class);
		} catch(Exception e) {
			System.out.println(url + " <- SERVIS KOJI PUKNE");
            e.printStackTrace();
			throw new SimpleException(SimpleResponseDTO.TIMEIO_API_ERROR);
		}

	    JsonNode locationsNode = locationsResponse.getBody().get("value");

        List<TimeIOLocationDTO> locations = new ArrayList<>();

        for (JsonNode locationNode : locationsNode) {
            try {
                TimeIOLocationDTO location = new TimeIOLocationDTO();
                Integer idIot = Integer.parseInt(locationNode.get("@iot.id").asText());
                String name = locationNode.get("name").asText();
   
                PointDTO point = new PointDTO();
                point.setLng(locationNode.get("location").get("coordinates").get(0).asDouble());
                point.setLat(locationNode.get("location").get("coordinates").get(1).asDouble());
    
                location.setIdIot(idIot);
                location.setName(name);
                location.setCoordinates(point);
    
                locations.add(location);
            } catch (Exception e) {
                continue;
            }
        }

        return locations;
    }

    @Override
    public List<TimeIOThingDTO> getAllThingsForLocation(Integer locationIdIot) throws SimpleException {
        ResponseEntity<JsonNode> thingsResponse = null;

        String url = TimeIOData.TIMEIO_BASE_URL + 
                     TimeIOData.TIMEIO_THING_SUFFIX.replace("locationID_Param", locationIdIot.toString());

		try {
			thingsResponse = restTemplate.exchange(url,
				                                   HttpMethod.GET, 
                                                   null, 
                                                   JsonNode.class);
		} catch(Exception e) {
			System.out.println(url + " <- SERVIS KOJI PUKNE");
            e.printStackTrace();
			throw new SimpleException(SimpleResponseDTO.TIMEIO_API_ERROR);
		}

	    JsonNode thingsNode = thingsResponse.getBody().get("Things");

        List<TimeIOThingDTO> things = new ArrayList<>();

        for (JsonNode thingNode : thingsNode) {
            try {
                TimeIOThingDTO thing = new TimeIOThingDTO();
                Integer idIot = Integer.parseInt(thingNode.get("@iot.id").asText());
                String name = thingNode.get("name").asText();
                String description = thingNode.get("description").asText();
    
                thing.setIdIot(idIot);
                thing.setName(name);
                thing.setDescription(description);
    
                TimeIOLocationDTO location = new TimeIOLocationDTO();
                location.setIdIot(locationIdIot);
                thing.setFkLocation(location);
    
                things.add(thing);
            } catch (Exception e) {
                continue;
            }
        }

        return things;

    }

    @Override
    public List<TimeIODatastreamDTO> getAllDatastreamsForThing(Integer thingIdIot) throws SimpleException {
        ResponseEntity<JsonNode> datastreamsResponse = null;

        String url = TimeIOData.TIMEIO_BASE_URL + TimeIOData.TIMEIO_DATASTREAM_SUFFIX.replace("thingID_Param", thingIdIot.toString());

        try {
            datastreamsResponse = restTemplate.exchange(url,
                                                        HttpMethod.GET, 
                                                        null, 
                                                        JsonNode.class);
        } catch(Exception e) {
            System.out.println(url + " <- SERVIS KOJI PUKNE");
            e.printStackTrace();
            throw new SimpleException(SimpleResponseDTO.TIMEIO_API_ERROR);
        }

        JsonNode datastreamsNode = datastreamsResponse.getBody().get("Datastreams");

        List<TimeIODatastreamDTO> datastreams = new ArrayList<>();
        
        for (JsonNode datastreamNode : datastreamsNode) {
            try {
                TimeIODatastreamDTO datastream = new TimeIODatastreamDTO();
                Integer idIot = Integer.parseInt(datastreamNode.get("@iot.id").asText());
                String name = datastreamNode.get("name").asText();
                String description = datastreamNode.get("description").asText();
                String unitOfMeasurementName = datastreamNode.get("unitOfMeasurement").get("name").asText();
                String unitOfMeasurementSymbol = datastreamNode.get("unitOfMeasurement").get("symbol").asText();

                datastream.setIdIot(idIot);
                datastream.setName(name);
                datastream.setDescription(description);
                datastream.setUnitMeasName(unitOfMeasurementName);
                datastream.setUnitMeasSymbol(unitOfMeasurementSymbol);

                TimeIOThingDTO thing = new TimeIOThingDTO();
                thing.setIdIot(thingIdIot);
                datastream.setFkThing(thing);

                datastreams.add(datastream);
            } catch (Exception e) {
                continue;
            }
        }

        return datastreams;

    }

    @Override
    public List<TimeIOObservationDTO> getAllObservationsForDatastream(TimeIODatastream datastream, Date dateFrom, Date dateTo) throws SimpleException {
        ResponseEntity<JsonNode> observationsResponse = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        String dateFromParamString = sdf.format(dateFrom);
        String dateToParamString = sdf.format(dateTo);

        String url = TimeIOData.TIMEIO_BASE_URL + 
                     TimeIOData.TIMEIO_OBSERVATION_SUFFIX.replace("datastreamID_Param", datastream.getIdIot().toString())
                                                         .replace("phenomenonTimeFrom_Param", dateFromParamString)
                                                         .replace("phenomenonTimeTo_Param", dateToParamString);
                 
        try {
            observationsResponse = restTemplate.exchange(url,
                                                            HttpMethod.GET, 
                                                            null, 
                                                            JsonNode.class);
        } catch(Exception e) {
            System.out.println(url + " <- SERVIS KOJI PUKNE");
            e.printStackTrace();
            throw new SimpleException(SimpleResponseDTO.TIMEIO_API_ERROR);
        }

        JsonNode observationsNode = observationsResponse.getBody().get("value");

        List<TimeIOObservationDTO> observations = new ArrayList<>();
           
        for (JsonNode observationNode : observationsNode) {
            try {
                Date phenomenonTime = sdf.parse(observationNode.get("phenomenonTime").asText());
                BigDecimal result = new BigDecimal(observationNode.get("result").asText());
                Long observationIdIot = observationNode.get("@iot.id").asLong();

                TimeIOObservationDTO observation = new TimeIOObservationDTO();
                observation.setId(observationIdIot);
                observation.setPhenomenonTime(phenomenonTime);
                observation.setResult(result);
                observation.setDatastreamId(datastream.getId());

                observations.add(observation);
            } catch (Exception e) {
                continue;
            }
        }

        return observations;

    }


}
