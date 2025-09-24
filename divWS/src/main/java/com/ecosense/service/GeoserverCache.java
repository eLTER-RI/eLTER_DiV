package com.ecosense.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Element;


@Service
public interface GeoserverCache {

    Element getOrFetch(String url) throws Exception;
    
    void clear();

}