package com.ecosense.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecosense.entity.EbvClass;


public interface EbvClassRepository extends JpaRepository<EbvClass, Integer> {

	  public Optional<EbvClass> findEbvClassByName(String name);

}
