package com.ecosense.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecosense.entity.TimeIOThing;

public interface TimeIOThingRepository extends JpaRepository<TimeIOThing, Integer> {
 
    TimeIOThing findByIdIot(Integer idIot);

}
