package com.ecosense.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecosense.dto.HabitatDTO;
import com.ecosense.exception.SimpleException;
import com.ecosense.service.HabitatService;
import com.ecosense.utils.VocabsData;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class HabitatServiceImpl implements HabitatService {
    
    @Autowired
	private RestTemplate restTemplate;

    @Override
    public List<HabitatDTO> getAll() throws SimpleException {
        List<HabitatDTO> habitats = new ArrayList<>();
		JsonNode resultNode = null;

		try {
			ResponseEntity<JsonNode> habitatResponse = restTemplate.exchange(VocabsData.BASE_URL + VocabsData.HABITAT_SUFFIX, HttpMethod.GET, null, JsonNode.class);
			resultNode = habitatResponse.getBody();

		} catch (Exception e) {
			System.out.println(VocabsData.BASE_URL + VocabsData.HABITAT_SUFFIX + " <- SERVIS KOJI PUKNE");
			return habitats;
		}

		JsonNode habitatListNode = resultNode.get("graph");

		int i = 0;
		for (JsonNode habitatFromApi : habitatListNode) {
			if (i > 2) {
				HabitatDTO habitatDTO = new HabitatDTO();
				habitatDTO.setName(habitatFromApi.get("prefLabel").get("value").asText());
				habitats.add(habitatDTO);
			}

			i++;
		}

        return habitats;
    }

}
