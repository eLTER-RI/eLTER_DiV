package com.ecosense.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecosense.dto.HabitatDTO;
import com.ecosense.dto.StandardObservationDTO;
import com.ecosense.exception.SimpleException;
import com.ecosense.service.StandardObservationService;
import com.ecosense.utils.VocabsData;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class StandardObservationServiceImpl implements StandardObservationService {
    
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<StandardObservationDTO> getAll() throws SimpleException {
        List<StandardObservationDTO> standardObservations = new ArrayList<>();

        List<StandardObservationDTO> atmosphere = getByGroup("Atmosphere");
        List<StandardObservationDTO> biosphere = getByGroup("Biosphere");
        List<StandardObservationDTO> geosphere = getByGroup("Geosphere");
        List<StandardObservationDTO> hydrosphere = getByGroup("Hydrosphere");
        List<StandardObservationDTO> sociosphere = getByGroup("Sociosphere");

        standardObservations.addAll(atmosphere);
        standardObservations.addAll(biosphere);
        standardObservations.addAll(geosphere);
        standardObservations.addAll(hydrosphere);
        standardObservations.addAll(sociosphere);

        return standardObservations;
    }

    private List<StandardObservationDTO> getByGroup(String suffix) {
        List<StandardObservationDTO> standardObservations = new ArrayList<>();
		JsonNode resultNode = null;

        try {
			ResponseEntity<JsonNode> standardObservationResponse = restTemplate.exchange(VocabsData.BASE_URL + VocabsData.STANDARD_OBSERVATION_SUFFIXES.get(suffix), HttpMethod.GET, null, JsonNode.class);
			resultNode = standardObservationResponse.getBody();

		} catch (Exception e) {
			System.out.println(VocabsData.BASE_URL + VocabsData.STANDARD_OBSERVATION_SUFFIXES.get(suffix) + " <- SERVIS KOJI PUKNE");
			return standardObservations;
		}

		JsonNode standardObservationListNode = resultNode.get("graph");

		int i = 0;
		for (JsonNode standardObservationFromApi : standardObservationListNode) {
			if (i != 0 && i < standardObservationListNode.size() - 1) {
				StandardObservationDTO soDTO = new StandardObservationDTO();
				soDTO.setName(standardObservationFromApi.get("prefLabel").get("value").asText());
                soDTO.setStandardObservationType(suffix);
				standardObservations.add(soDTO);
			}

			i++;
		}

        return standardObservations;
    }

}
