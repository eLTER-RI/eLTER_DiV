package com.ecosense.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ecosense.service.GeoserverCache;

@Service
public class GeoserverCacheImpl implements GeoserverCache {

    private final Map<String, Element> cache = new ConcurrentHashMap<>();

    @Override
    public Element getOrFetch(String url) throws Exception{
        if (cache.containsKey(url)) {
            return cache.get(url);
        } else {
            return fetchFromGeoServer(url);
        }
    }

    private Element fetchFromGeoServer(String url) throws Exception {
        StringBuilder response = getResponseFromGeoserverAPI(url);

        Element capabilityElement = extractCapabilityXmlElement(response);

        cache.put(url, capabilityElement);

        return capabilityElement;
    }

    private StringBuilder getResponseFromGeoserverAPI(String url) {
        BufferedReader in = null;
        try {
            URL geoserverUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) geoserverUrl.openConnection();
            connection.setRequestMethod("GET");

            // Read the response into a string
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            return response;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Element extractCapabilityXmlElement(StringBuilder response) throws Exception {
        if (response == null)   return null;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(response.toString())));
        Element root = document.getDocumentElement();

        NodeList capabilityNodes = root.getElementsByTagName("Capability");

        Element capabilityElement = null;
        if (capabilityNodes.getLength() > 0) {
            capabilityElement = (Element) capabilityNodes.item(0);
        }

        return capabilityElement;
    }

    @Override
    public void clear() {
        cache.clear();
    }

}
