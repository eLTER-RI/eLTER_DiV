package com.ecosense.service;

import org.springframework.stereotype.Service;

@Service
public interface GeoserverService {

    void refreshGeoserverData() throws Exception;

}
                    