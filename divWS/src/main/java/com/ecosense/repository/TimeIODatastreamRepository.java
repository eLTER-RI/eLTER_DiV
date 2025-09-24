package com.ecosense.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecosense.entity.TimeIODatastream;
import com.ecosense.entity.TimeIOLocation;

public interface TimeIODatastreamRepository extends JpaRepository<TimeIODatastream, Integer> {

    TimeIODatastream findByIdIot(Integer idIot);

}
