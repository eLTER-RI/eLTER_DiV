package com.ecosense.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecosense.entity.EbvEntityType;

public interface EbvEntityTypeRepository extends JpaRepository<EbvEntityType, Integer> {

	Optional<EbvEntityType> findEbvEntityTypeByName(String name);

}
