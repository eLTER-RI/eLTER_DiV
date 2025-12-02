package com.ecosense.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecosense.dto.DivFilterDTO;
import com.ecosense.dto.input.FilterSiteIDTO;
import com.ecosense.dto.output.ActivityODTO;
import com.ecosense.dto.output.CountryODTO;
import com.ecosense.dto.output.SitesODTO;
import com.ecosense.dto.output.SiteDetailsODTO;
import com.ecosense.dto.output.SiteODTO;
import com.ecosense.exception.SimpleException;

@Service
public interface SiteService {


	void addAllToDB() throws SimpleException;
	
	SitesODTO getAllPolygons() throws SimpleException, IOException;

	void refreshDatabase() throws SimpleException;

	List<SiteODTO> getAllPoints() throws SimpleException;

	List<ActivityODTO> getAllActivities() throws SimpleException;

	List<CountryODTO> getAllCountries() throws SimpleException;

	List<String> getAllTitles() throws SimpleException;

	SitesODTO filterSites(FilterSiteIDTO filterSiteIDTO) throws SimpleException, IOException;

	SiteDetailsODTO getSiteInfo(Integer siteId) throws SimpleException, SQLException, IOException;

	SiteODTO getSite(Integer siteId) throws SimpleException;

	String getGeoJsonPolygon(Integer id);

	SitesODTO filterAndSearchSites(DivFilterDTO divFilterDTO) throws SimpleException, IOException;

	String createUrlFromDivFilter(DivFilterDTO divFilterDTO);

}
