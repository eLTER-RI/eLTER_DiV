package com.ecosense.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecosense.service.TimeIOService;

@Component
public class ScheduledTimeIO {
    
    private static final Logger log = LoggerFactory.getLogger(ScheduledGeoserverReadingData.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired TimeIOService timeIOService;

    @Scheduled(cron = "0 0 0 * * *")
    public void refreshGeoserverData() {
        try {
            log.info("Before reading TimeIO" + dateFormat.format(new Date()));
            timeIOService.refreshTimeIO();
            log.info("After reading TimeIO " + dateFormat.format(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }   

}
