package com.ecosense.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecosense.entity.BoundingBox;

public interface BoundingBoxRepository extends JpaRepository<BoundingBox, Integer> {
    
}
