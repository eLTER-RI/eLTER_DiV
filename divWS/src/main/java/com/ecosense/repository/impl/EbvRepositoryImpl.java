package com.ecosense.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.ecosense.dto.EbvDTO;
import com.ecosense.dto.input.EbvFilterIDTO;
import com.ecosense.entity.Ebv;
import com.ecosense.repository.EbvRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class EbvRepositoryImpl implements EbvRepo {

	@PersistenceContext	private EntityManager em;
	
	@Override
	public List<EbvDTO> filterEbv(EbvFilterIDTO ebvFilter) {
		List<EbvDTO> response = new ArrayList<>();
		
		StringBuilder sqlSB = new StringBuilder("SELECT e FROM Ebv e WHERE 1=1 ");
		HashMap<String, Object> params = new HashMap<>();
		
		if (ebvFilter.getCreator() != null) {
			sqlSB.append("AND UPPER(e.creatorName) LIKE :creatorName ");
			params.put("creatorName", "%" + ebvFilter.getCreator().toUpperCase().trim() + "%");
		}
		
		if (ebvFilter.getDate() != null) {
			sqlSB.append("AND e.dateCreated >= :dateCreated ");
			params.put("dateCreated", ebvFilter.getDate());
		}
		
		if (ebvFilter.getEbvClass() != null) {
			sqlSB.append("AND e.ebvClass.id = :ebvClassId ");
			params.put("ebvClassId", ebvFilter.getEbvClass().getId());
		}
		
		if (ebvFilter.getEbvName() != null) {
			sqlSB.append("AND e.ebvName.id = :ebvNameId ");
			params.put("ebvNameId", ebvFilter.getEbvName().getId());
		}
		
		if (ebvFilter.getEntityType() != null) {
			sqlSB.append("AND e.ebvEntityType.id = :ebvEntityTypeId ");
			params.put("ebvEntityTypeId", ebvFilter.getEntityType().getId());
		}
		
		if (ebvFilter.getSpatialScope() != null) {
			sqlSB.append("AND e.ebvSpatialScope.id = :ebvSpatialScopeId ");
			params.put("ebvSpatialScopeId", ebvFilter.getSpatialScope().getId());
		}
		
		sqlSB.append("ORDER BY e.title ");
		Query query = em.createQuery(sqlSB.toString(), Ebv.class);
		
		for(String key :params.keySet()) {
			query.setParameter(key, params.get(key));
		}
		
		List<Ebv> ebvEntities = query.getResultList();
		
		for (Ebv ebv : ebvEntities) {
			response.add(new EbvDTO(ebv.getId(), ebv.getEbvId(), ebv.getTitle()));
		}
		
		return response;
	}
}
