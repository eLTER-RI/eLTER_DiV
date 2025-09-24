package com.ecosense.service;

import java.util.Date;
import java.util.List;

import com.ecosense.dto.timeio.TimeIODatastreamDTO;
import com.ecosense.dto.timeio.TimeIOLocationDTO;
import com.ecosense.dto.timeio.TimeIOObservationDTO;
import com.ecosense.dto.timeio.TimeIOThingDTO;
import com.ecosense.entity.TimeIODatastream;
import com.ecosense.exception.SimpleException;

public interface TimeIOHttpService {

    List<TimeIOLocationDTO> getAllLocations() throws SimpleException;
    
    List<TimeIOThingDTO> getAllThingsForLocation(Integer locationIdIot) throws SimpleException;

    List<TimeIODatastreamDTO> getAllDatastreamsForThing(Integer thingIdIot) throws SimpleException;
    
    List<TimeIOObservationDTO> getAllObservationsForDatastream(TimeIODatastream datastream, Date dateFrom, Date dateTo) throws SimpleException;

}
