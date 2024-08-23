package com.ecosense.utils;

import java.util.HashMap;
import java.util.Map;

public class VocabsData {
    
    public static final String BASE_URL = "https://vocabs.lter-europe.net";

    public static final String HABITAT_SUFFIX = "/rest/v1/so/data?uri=https://vocabs.lter-europe.net/so/c1&format=application/json";

    public static final Map<String, String> STANDARD_OBSERVATION_SUFFIXES  = new HashMap<String, String>() {{
        put("Atmosphere", "/rest/v1/so/data?uri=https://vocabs.lter-europe.net/so/c5&format=application/json");
        put("Biosphere", "/rest/v1/so/data?uri=https://vocabs.lter-europe.net/so/c4&format=application/json");
        put("Geosphere", "/rest/v1/so/data?uri=https://vocabs.lter-europe.net/so/c2&format=application/json");
        put("Hydrosphere", "/rest/v1/so/data?uri=https://vocabs.lter-europe.net/so/c3&format=application/json");
        put("Sociosphere", "/rest/v1/so/data?uri=https://vocabs.lter-europe.net/so/c1&format=application/json");
    }};

}
