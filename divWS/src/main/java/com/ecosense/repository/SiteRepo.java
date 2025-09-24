package com.ecosense.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.input.FilterSiteIDTO;
import com.ecosense.entity.Site;
import com.ecosense.exception.SimpleException;

@Repository
public interface SiteRepo {

	void insertSite(Site site);

    List<Site> getAllPolygons() throws SimpleException;

	Site getSite(String idSuffix);
	
	Site getSite(Integer id);

	void updateSite(Site siteFromDB);

	List<Site> getAllPoints();

	List<String> getAllTitles();

	List<Object[]> filterSites(FilterSiteIDTO filterIDTO);

	BoundingBoxDTO getBoundingBox32643 (Integer siteId) throws SimpleException;

	void createCircle(Site site, Double r);

	BoundingBoxDTO getBoundingBox4326(Integer siteId) throws SimpleException;

}
