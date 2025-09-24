package com.ecosense.service.impl;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecosense.dto.timeio.TimeIODatastreamDTO;
import com.ecosense.dto.timeio.TimeIOLocationDTO;
import com.ecosense.dto.timeio.TimeIOThingDTO;
import com.ecosense.entity.TimeIODatastream;
import com.ecosense.entity.TimeIOLocation;
import com.ecosense.entity.TimeIOThing;
import com.ecosense.repository.TimeIODatastreamRepository;
import com.ecosense.repository.TimeIOThingRepository;
import com.ecosense.repository.impl.TimeIOLocationRepository;
import com.ecosense.service.TimeIOHttpService;
import com.ecosense.service.TimeIOService;

@Service
public class TimeIOServiceImpl implements TimeIOService {

    @Autowired private TimeIOLocationRepository timeIOLocationRepository;
    @Autowired private TimeIOThingRepository timeIOThingRepository;
    @Autowired private TimeIODatastreamRepository timeIODatastreamRepository;

    @Autowired private TimeIOHttpService timeIOHttpService;

    @Override
    public void refreshTimeIO() throws Exception {
        // get all locations from url
        List<TimeIOLocationDTO> locationsDTO = timeIOHttpService.getAllLocations();
        
        for (TimeIOLocationDTO locationDTO : locationsDTO) {

            // check if location exists in db
            TimeIOLocation timeIOLocation = timeIOLocationRepository.findByIdIot(locationDTO.getIdIot());
            
            // save location to db / or update if exists
            timeIOLocation = insertOrUpdateLocation(locationDTO, timeIOLocation);

            List<TimeIOThingDTO> thingsDTO = timeIOHttpService.getAllThingsForLocation(locationDTO.getIdIot());
            for (TimeIOThingDTO thingDTO : thingsDTO) {
                // check if thing exists in db
                TimeIOThing timeIOThing = timeIOThingRepository.findByIdIot(thingDTO.getIdIot());
                
                // save thing to db / or update if exists
                timeIOThing = insertOrUpdateThing(thingDTO, timeIOThing);

                List<TimeIODatastreamDTO> datastreamsDTO = timeIOHttpService.getAllDatastreamsForThing(thingDTO.getIdIot());
                for (TimeIODatastreamDTO datastreamDTO : datastreamsDTO) {
                    // check if datastream exists in db
                    TimeIODatastream timeIODatastream = timeIODatastreamRepository.findByIdIot(datastreamDTO.getIdIot());
                    
                    // save datastream to db / or update if exists
                    timeIODatastream = insertOrUpdateDatastream(datastreamDTO, timeIODatastream); 
                }
            }

        }

    }

    public TimeIOLocation insertOrUpdateLocation(TimeIOLocationDTO locationDTO, TimeIOLocation timeIOLocation) {
        if (timeIOLocation == null) {
            timeIOLocation = new TimeIOLocation();
        }

        timeIOLocation.setIdIot(locationDTO.getIdIot());
        timeIOLocation.setName(locationDTO.getName());

        Coordinate coordinate = new Coordinate(locationDTO.getCoordinates().getLng(), locationDTO.getCoordinates().getLat());
        Point point = new GeometryFactory().createPoint(coordinate);
        timeIOLocation.setCoordinates(point);

        return timeIOLocationRepository.save(timeIOLocation);
    }

    public TimeIOThing insertOrUpdateThing(TimeIOThingDTO thingDTO, TimeIOThing timeIOThing) {
        if (timeIOThing == null) {
            timeIOThing = new TimeIOThing();
        }

        timeIOThing.setIdIot(thingDTO.getIdIot());
        timeIOThing.setName(thingDTO.getName());
        timeIOThing.setDescription(thingDTO.getDescription());

        timeIOThing.setLocation(timeIOLocationRepository.findByIdIot(thingDTO.getFkLocation().getIdIot()));

        return timeIOThingRepository.save(timeIOThing);
    }

    public TimeIODatastream insertOrUpdateDatastream(TimeIODatastreamDTO datastreamDTO, TimeIODatastream timeIODatastream) {
        if (timeIODatastream == null) {
            timeIODatastream = new TimeIODatastream();
        }

        timeIODatastream.setIdIot(datastreamDTO.getIdIot());
        timeIODatastream.setName(datastreamDTO.getName());
        timeIODatastream.setDescription(datastreamDTO.getDescription());
        timeIODatastream.setUnitMeasName(datastreamDTO.getUnitMeasName());
        timeIODatastream.setUnitMeasSymbol(datastreamDTO.getUnitMeasSymbol());

        timeIODatastream.setThing(timeIOThingRepository.findByIdIot(datastreamDTO.getFkThing().getIdIot()));

        return timeIODatastreamRepository.save(timeIODatastream);
    }
    
}
