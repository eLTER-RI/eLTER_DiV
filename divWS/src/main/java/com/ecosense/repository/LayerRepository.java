package com.ecosense.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecosense.entity.Layer;
import com.ecosense.entity.LayerGroup;

public interface LayerRepository extends JpaRepository<Layer, Integer> {

	Layer findLayerByCode(String code);
	
	@Query("SELECT l FROM Layer l WHERE l.active = true "
			+ "AND (:isLayerTypeEmpty = TRUE OR l.layerType IN (:layertype)) " // Check the flag
			+ "AND (CAST(:code AS text) IS NULL OR l.code LIKE :code) "
			+ "AND (:isIdsEmpty = TRUE OR l.id IN (:ids)) " // Check the flag
			+ "ORDER BY l.name")
	List<Layer> getAllActive(
			@Param("layertype") List<String> layertype,
			@Param("isLayerTypeEmpty") Boolean isLayerTypeEmpty,
			@Param("code") String code,
			@Param("ids") List<Integer> ids,
			@Param("isIdsEmpty") Boolean isIdsEmpty);

	@Query("SELECT l FROM Layer l WHERE l.active = true and l.code =:code")
	Layer findActiveLayerByCode(@Param("code") String layerName);
	
	@Query("SELECT distinct l.layerGroup FROM Layer l WHERE l.active = true and l.layerType in ( :layertype) ")
	List<LayerGroup> getLayerGroupActive(@Param("layertype") List<String> layertype);
	
	@Query("SELECT distinct l.layerGroup FROM Layer l WHERE l.active = true and l.layerGroup.id = :lgId ")
	List<LayerGroup> getByLayerGroupId(@Param("lgId") Integer lgId);

	List<Layer> findBySiteUuid(String siteUuid);

	Layer findByCode(String code);	

}
