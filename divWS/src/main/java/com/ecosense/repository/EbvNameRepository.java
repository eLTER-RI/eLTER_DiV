package com.ecosense.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecosense.entity.EbvName;

public interface EbvNameRepository extends JpaRepository<EbvName, Integer> {

	Optional<EbvName> findEbvNameByName(String name);

	@Query("SELECT ebvName FROM EbvName ebvName WHERE ebvName.ebvClass.id =:ebvClassId ORDER BY ebvName.name")
	List<EbvName> findByEbvClass(@Param("ebvClassId") Integer ebvClassId);

}
