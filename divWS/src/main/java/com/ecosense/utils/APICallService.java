package com.ecosense.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.exception.SimpleException;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class APICallService {

    private RestTemplate restTemplate;

    public APICallService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JsonNode getRequest(String url) throws SimpleException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<JsonNode> response = null;
		try {
			response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
			return response != null ? response.getBody() : null;
		} catch (Exception e) {
			System.out.println(url + " <- SERVIS KOJI PUKNE");
			e.printStackTrace();
			throw new SimpleException(SimpleResponseDTO.GENERAL_API_ERROR);
		}
	}

}
