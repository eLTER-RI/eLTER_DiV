package com.ecosense.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecosense.dto.StandardObservationDTO;
import com.ecosense.exception.SimpleException;

@Service
public interface StandardObservationService {
    
    List<StandardObservationDTO> getAll() throws SimpleException;

}
