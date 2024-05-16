package com.ecosense.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecosense.service.DarService;
import com.ecosense.utils.DARData;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class DarServiceImpl implements DarService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<JsonNode> get(String deimsUUID) {
        List<JsonNode> response = new ArrayList<>();
        String url = DARData.DEIMS_URL + deimsUUID;

        do {
            ResponseEntity<JsonNode> responseAll = restTemplate.exchange(url, HttpMethod.GET,
                    null,
                    JsonNode.class);
            JsonNode body = responseAll.getBody();

            if (body != null && !body.isNull()) {
                Integer numFound = body.get("numFound").asInt();

                if (numFound > 0 && body.get("results") != null && body.get("results").isArray()) {
                    for (JsonNode result : body.get("results")) {
                        String identifier = result.get("identifier").asText();

                        ResponseEntity<JsonNode> responseIdentifier = restTemplate
                                .exchange(DARData.ONE_DATASET_URL + identifier, HttpMethod.GET, null, JsonNode.class);
                        JsonNode bodyIdentifier = responseIdentifier.getBody();

                        if (bodyIdentifier != null) {
                            response.add(bodyIdentifier);
                        }
                    }
                }
            }

            String nextPage = body == null || body.get("nextPage") == null ? null : body.get("nextPage").asText();
            url = nextPage == null ? null : nextPage.replace("http", "https");
        } while (url != null);

        return response;
    }

}
