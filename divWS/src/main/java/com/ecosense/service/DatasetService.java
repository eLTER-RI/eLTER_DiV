package com.ecosense.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecosense.dto.DivFilterDTO;
import com.ecosense.dto.output.DatasetODTO;
import com.ecosense.dto.output.DatasetsODTO;
import com.ecosense.exception.SimpleException;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public interface DatasetService {

    DatasetsODTO filterAndSearchDataset(DivFilterDTO divFilterDTO) throws SimpleException, IOException;

    List<DatasetODTO> getAllExternalDatasets() throws SimpleException, IOException;

    DatasetODTO parseDatasetFromNode(JsonNode hitNode, Boolean setDatasetJson);
    
    DatasetODTO getForLayer(Integer layerId) throws SimpleException;

}
