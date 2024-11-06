package com.ecosense.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.ecosense.dto.DivFilterDTO;
import com.ecosense.dto.output.DatasetsODTO;
import com.ecosense.exception.SimpleException;

@Service
public interface DatasetService {

    DatasetsODTO filterAndSearchDataset(DivFilterDTO divFilterDTO) throws SimpleException, IOException;
    
}
