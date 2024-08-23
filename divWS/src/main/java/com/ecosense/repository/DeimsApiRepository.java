package com.ecosense.repository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ecosense.dto.output.SiteDetailsODTO;
import com.ecosense.entity.Activity;
import com.ecosense.entity.Country;
import com.ecosense.entity.Site;
import com.ecosense.exception.SimpleException;

@Repository
public interface DeimsApiRepository {

	List<Site> getAllSites() throws SimpleException;

	SiteDetailsODTO getSiteInfo(String siteIdSuffix) throws SimpleException, SQLException, IOException;

	Site getPolygon(String siteIdSuffix) throws SimpleException;

	List<Activity> getActivities(String siteIdSuffix) throws SimpleException;

	List<Country> getCountries(String siteIdSuffix) throws SimpleException;
}
