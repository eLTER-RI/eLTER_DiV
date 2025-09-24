
package com.ecosense.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecosense.dto.CapabilitiesRequestDTO;
import com.ecosense.dto.DivFilterDTO;
import com.ecosense.dto.LayerDTO;
import com.ecosense.dto.DatasetLayerDTO;
import com.ecosense.dto.input.FeatureInfoRequestIDTO;
import com.ecosense.dto.output.LayerGroupDTO;
import com.ecosense.exception.SimpleException;

@Service
public interface LayerService {

	List<LayerDTO> getAll(List<String> layertypes, List<Integer> ids, String code) throws SimpleException;

	Boolean addTimeForSnowCover() throws SimpleException;

	String getFeatureInfoHTML(FeatureInfoRequestIDTO firDB) throws Exception;

	List<LayerGroupDTO> getLayerGroups(List<String> layertype, Integer groupdId) throws SimpleException;

	String getCapabilities(CapabilitiesRequestDTO capabilitiesRequestDTO);

	List<DatasetLayerDTO> filterAndSearch(DivFilterDTO divFilterDTO) throws SimpleException;

}
