package com.ecosense.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecosense.dto.EbvClassDTO;
import com.ecosense.dto.EbvDTO;
import com.ecosense.dto.EbvDetailDTO;
import com.ecosense.dto.EbvEntityTypeDTO;
import com.ecosense.dto.EbvNameDTO;
import com.ecosense.dto.EbvSpatialScopeDTO;
import com.ecosense.dto.input.EbvFilterIDTO;

@Service
public interface EbvService {

	public void refreshEbv();

	public List<EbvClassDTO> getAllEbvClass();
	public List<EbvNameDTO> getAllEbvName();
	public List<EbvEntityTypeDTO> getAllEbvEntityType();
	public List<EbvSpatialScopeDTO> getAllEbvSpatialScope();

	public EbvDetailDTO getDetail(Integer id);

	public List<EbvDTO> filterEbv(EbvFilterIDTO ebvFilter);

	void refreshDataset();

	public List<String> getAllCreators();

	public List<EbvNameDTO> getEbvName(Integer ebvClassId);
}
