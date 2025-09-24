package com.ecosense.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecosense.entity.LayerGroup;

public interface LayerGroupRepository extends JpaRepository<LayerGroup, Integer> {

    Optional<LayerGroup> findByNameIgnoreCase(String name);

}
