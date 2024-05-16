package com.ecosense.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecosense.entity.TimeSeries;

public interface TimeseriesJpaRepo extends JpaRepository<TimeSeries, Integer> {
	
	@Query("SELECT ts FROM TimeSeries ts WHERE ts.sosId =:sosId AND ts.label =:label")
	List<TimeSeries> getBySosIdAndLabel(@Param("sosId") Integer sosId, @Param("label") String label);
	

}
