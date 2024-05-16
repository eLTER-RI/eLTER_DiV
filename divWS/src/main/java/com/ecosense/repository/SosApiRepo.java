package com.ecosense.repository;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ecosense.dto.output.MeasurementsODTO;
import com.ecosense.entity.Phenomenon;
import com.ecosense.entity.Procedure;
import com.ecosense.entity.SosPath;
import com.ecosense.entity.Station;
import com.ecosense.entity.TimeSeries;

@Repository
public interface SosApiRepo {

	MeasurementsODTO getMeasurements(TimeSeries timeSeries, Date dateFrom, Date dateTo);

	List<Phenomenon> getPhenomenons(String url);

	List<Procedure> getProcedures(String url);

	List<Station> getStations(SosPath sosPath);

	List<TimeSeries> getTimeSeries(String url);

	String translatePhenomenon(String domainId);

	Boolean isProviderValid(String provider);

	
}
