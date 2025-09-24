package com.ecosense.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonNodeConverter {

    public static JsonNode convertStringToJsonNode(String jsonString) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(jsonString);
    }

}
