package com.ecosense.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecosense.entity.SosPath;

public interface SosPathRepo extends JpaRepository<SosPath, Integer> {
	
	@Query("SELECT sp FROM SosPath sp WHERE sp.id =:id ")
	Optional<SosPath> findById(@Param("id") Integer id);

	@Query("SELECT sp FROM SosPath sp WHERE sp.active = true ")
	List<SosPath> findAllActive();
}
