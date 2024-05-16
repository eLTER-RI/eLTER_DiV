package com.ecosense.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecosense.entity.Procedure;

public interface ProcedureJpaRepo extends JpaRepository<Procedure, Integer> {
	
	@Query("SELECT p FROM Procedure p WHERE p.sosId =:sosId AND p.label =:label")
	List<Procedure> getBySosIdAndLabel(@Param("sosId")Integer sosId, @Param("label")String label);

	@Query("SELECT p FROM Procedure p WHERE p.sosId =:sosId")
	List<Procedure> getBySosId(@Param("sosId")Integer id);

}
