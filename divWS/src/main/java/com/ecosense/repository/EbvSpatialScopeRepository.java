package com.ecosense.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecosense.entity.EbvSpatialScope;

public interface EbvSpatialScopeRepository extends JpaRepository<EbvSpatialScope, Integer> {

	Optional<EbvSpatialScope> findEbvSpatialScopeByName(String name);

}
