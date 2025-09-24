package com.ecosense.service;

import org.springframework.stereotype.Service;

import com.ecosense.dto.timeio.TimeIOLocationDTO;
import com.ecosense.dto.timeio.TimeIOLocationsDTO;
import com.ecosense.exception.SimpleException;

@Service
public interface TimeIOLocationService {

    TimeIOLocationsDTO getAllLocations() throws SimpleException;

    TimeIOLocationDTO getLocationDetails(Integer id) throws SimpleException;

}
