package com.ecosense.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecosense.service.GeoserverService;

@Component
public class ScheduledGeoserverReadingData {

    private static final Logger log = LoggerFactory.getLogger(ScheduledGeoserverReadingData.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired GeoserverService geoserverService;

    @Scheduled(cron = "0 0 0 * * *")
    public void refreshGeoserverData() {
        try {
            log.info("Before refreshGeoserverData " + dateFormat.format(new Date()));
            geoserverService.refreshGeoserverData();
            log.info("After refreshGeoserverData " + dateFormat.format(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }   

}
