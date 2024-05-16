package com.ecosense.repository;

import java.util.List;

import com.ecosense.dto.input.FilterStationIDTO;
import com.ecosense.entity.Station;
import com.ecosense.exception.SimpleException;

public interface StationRepoSpec {

	List<Station> filterStations(FilterStationIDTO filterStationIDTO) throws SimpleException;
	
}
