package com.ecosense.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecosense.entity.Station;

public interface StationJpaRepo extends JpaRepository<Station, Integer>, StationRepoSpec {
	
	@Query("SELECT s FROM Station s WHERE s.sosPath.id =:sosPathId")
	List<Station> findStationBySosPathId(@Param("sosPathId") Integer sosPathId);

	@Query("SELECT s.sosPath.url FROM Station s WHERE s.id =:stationId")
	String getUrlByStationId(@Param("stationId") Integer stationId);

	@Query("SELECT s FROM Station s WHERE s.sosId =:sosId AND s.sosPath.id =:sosPathId")
	List<Station> getBySosId(@Param("sosId")Integer sosId,@Param("sosPathId") Integer sosPathId);



}
