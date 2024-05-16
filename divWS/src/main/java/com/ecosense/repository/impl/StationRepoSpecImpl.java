package com.ecosense.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.input.FilterStationIDTO;
import com.ecosense.entity.Station;
import com.ecosense.exception.SimpleException;
import com.ecosense.repository.StationRepoSpec;

public class StationRepoSpecImpl implements StationRepoSpec {

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public List<Station> filterStations(FilterStationIDTO filterStationIDTO) throws SimpleException {
		 StringBuilder querySB = new StringBuilder("SELECT DISTINCT station FROM Station station ");
		 
		 if (filterStationIDTO == null) {
			 throw new SimpleException(SimpleResponseDTO.DATA_NOT_COMPLETE);
		 }
		 
		 if (filterStationIDTO.getPhenomenonsLabels() != null && !filterStationIDTO.getPhenomenonsLabels().isEmpty()) {
			 querySB.append("LEFT JOIN station.timeseries timeseries ");
		 }
		 
		 querySB.append("WHERE 1=1 ");
		 Map<String, Object> parameters = new HashMap<>();
		 
		 if (filterStationIDTO.getPhenomenonsLabels() != null && !filterStationIDTO.getPhenomenonsLabels().isEmpty()) {
			 querySB.append("AND timeseries.phenomenon.label IN ( :phenomenonsLabel) ");
			 parameters.put("phenomenonsLabel", filterStationIDTO.getPhenomenonsLabels());
		 }
		 
		 if (filterStationIDTO.getSosPathUrls() != null && !filterStationIDTO.getSosPathUrls().isEmpty()) {
			 querySB.append("AND station.sosPath.url IN ( :sosPathUrls) ");
			 parameters.put("sosPathUrls", filterStationIDTO.getSosPathUrls());
		 }
		 
		 System.out.println(querySB.toString());
		 Query query = em.createQuery(querySB.toString(), Station.class);
		 for(String key : parameters.keySet()) {
			 query.setParameter(key, parameters.get(key));
		 }
		 
		 List<Station> stations = query.getResultList();
		 return stations;
	}
	
}
