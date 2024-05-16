package com.ecosense.service;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.input.FilterSatelliteIDTO;
import com.ecosense.dto.output.SatelliteODTO;
import com.ecosense.exception.SimpleException;

@Service
public interface SatelliteService {


	List<SatelliteODTO> getMapList(FilterSatelliteIDTO filterSatelliteIDTO, Integer siteId) throws SimpleException;

	byte[] getImage(Integer siteId, Integer pkLayer, Boolean keepAspectRatio, Double maxX, Double maxY, Double minX,
			Double minY) throws SimpleException;

}
