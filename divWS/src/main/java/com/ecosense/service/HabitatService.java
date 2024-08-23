package com.ecosense.service;

import java.util.List;

import com.ecosense.dto.HabitatDTO;
import com.ecosense.exception.SimpleException;

public interface HabitatService {

    List<HabitatDTO> getAll() throws SimpleException;
    
}
