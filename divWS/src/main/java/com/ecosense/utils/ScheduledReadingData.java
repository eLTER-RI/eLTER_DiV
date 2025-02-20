package com.ecosense.utils;

import org.springframework.stereotype.Component;

import com.ecosense.exception.SimpleException;
import com.ecosense.service.EbvService;
import com.ecosense.service.LayerService;
import com.ecosense.service.SiteService;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

@Component
public class ScheduledReadingData {

	private static final Logger log = LoggerFactory.getLogger(ScheduledReadingData.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	@Autowired
	private SiteService siteService;
	
	@Autowired
	private EbvService ebvService;
	
	@Autowired
	private LayerService layerService;
	
	@Scheduled(cron = "0 0 0 * * *")  //svaki dan u ponoc 
	public void refreshDEIMS() {
		try {
			log.info("The time before refreshDatabase", dateFormat.format(new Date()));
			siteService.refreshDatabase();
			log.info("The time after refreshDatabase", dateFormat.format(new Date()));
		} catch (SimpleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Scheduled(cron = "0 0 3 * * ?")  // svaki dan u 3am
	public void refreshEBV() {
		log.info("The time before refreshDatabase", dateFormat.format(new Date()));
		ebvService.refreshEbv();
		log.info("The time after refreshDatabase", dateFormat.format(new Date()));
	}
	
	@Scheduled(cron = "0 0 5 * * ?")  // svaki dan u 5am
	public void refreshTimesForSnowCover() {
		log.info("The time before refreshDatabase", dateFormat.format(new Date()));
		try {
			layerService.addTimeForSnowCover();
		} catch (SimpleException e) {
			log.info("TimesForSnowCover is not successiful ", dateFormat.format(new Date()));
			e.printStackTrace();
		}
		log.info("The time after refreshDatabase", dateFormat.format(new Date()));
	}
}
