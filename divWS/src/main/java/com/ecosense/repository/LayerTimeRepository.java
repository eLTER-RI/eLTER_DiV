package com.ecosense.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecosense.entity.Layer;
import com.ecosense.entity.LayerTime;

public interface LayerTimeRepository extends JpaRepository<LayerTime, Integer> {
	
	@Query("SELECT lt FROM LayerTime lt WHERE lt.layer.code =:code and lt.availableTime = :time")
	List<LayerTime> findByNameAndTime(@Param("time") String time, @Param("code") String code);

	Long countByLayer(Layer layer);

	void deleteByLayer(Layer layer);

}
