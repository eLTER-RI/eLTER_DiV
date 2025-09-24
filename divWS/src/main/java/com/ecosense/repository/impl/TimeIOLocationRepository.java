package com.ecosense.repository.impl;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecosense.entity.TimeIOLocation;

public interface TimeIOLocationRepository extends JpaRepository<TimeIOLocation, Integer> {

    TimeIOLocation findByIdIot(Integer idIot);

}
