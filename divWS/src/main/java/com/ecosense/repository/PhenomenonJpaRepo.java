package com.ecosense.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecosense.entity.Phenomenon;

public interface PhenomenonJpaRepo extends JpaRepository<Phenomenon, Integer> {

	@Query("SELECT p FROM Phenomenon p WHERE p.sosId =:sosId AND p.label =:label ")
	List<Phenomenon> getBySosIdAndLabel(@Param("sosId")Integer sosId, @Param("label")String label);
	
	@Query("SELECT p FROM Phenomenon p WHERE p.sosId =:sosId")
	List<Phenomenon> getBySosId(@Param("sosId")Integer sosId);
	
	@Query("SELECT distinct p.label FROM Phenomenon p ORDER BY p.label")
	List<String> findPhenomenonsDistinct();
}
