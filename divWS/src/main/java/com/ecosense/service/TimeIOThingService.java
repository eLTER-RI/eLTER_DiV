package com.ecosense.service;

import org.springframework.stereotype.Service;

import com.ecosense.dto.timeio.TimeIOThingDTO;
import com.ecosense.exception.SimpleException;

@Service
public interface TimeIOThingService {

    TimeIOThingDTO getThingDetails(Integer id) throws SimpleException;

}
