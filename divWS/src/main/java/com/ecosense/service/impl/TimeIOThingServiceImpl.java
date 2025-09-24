package com.ecosense.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.timeio.TimeIODatastreamDTO;
import com.ecosense.dto.timeio.TimeIOThingDTO;
import com.ecosense.entity.TimeIODatastream;
import com.ecosense.entity.TimeIOThing;
import com.ecosense.exception.SimpleException;
import com.ecosense.repository.TimeIOThingRepository;
import com.ecosense.service.TimeIOThingService;

@Service
public class TimeIOThingServiceImpl implements TimeIOThingService {

    @Autowired private TimeIOThingRepository timeIOThingRepo;

    @Override
    public TimeIOThingDTO getThingDetails(Integer id) throws SimpleException {
        TimeIOThing thing = timeIOThingRepo.findById(id)
                                           .orElseThrow(() -> new SimpleException(SimpleResponseDTO.DATA_NOT_EXIST));
        
        TimeIOThingDTO thingDTO = new TimeIOThingDTO();
        thingDTO.setId(id);
        thingDTO.setName(thing.getName());
        thingDTO.setDescription(thing.getDescription());
        thingDTO.setIdIot(thing.getIdIot());
        
        List<TimeIODatastream> datastreams = thing.getDatastreams();
        List<TimeIODatastreamDTO> datastreamsDTO = new ArrayList<>();
        
        for (TimeIODatastream datastream : datastreams) {
            TimeIODatastreamDTO datastreamDTO = new TimeIODatastreamDTO();
            datastreamDTO.setId(datastream.getId());
            datastreamDTO.setName(datastream.getName());
            datastreamDTO.setDescription(datastream.getDescription());
            datastreamDTO.setUnitMeasName(datastream.getUnitMeasName());
            datastreamDTO.setUnitMeasSymbol(datastream.getUnitMeasSymbol());
            datastreamsDTO.add(datastreamDTO);
        }
        thingDTO.setFkDatastreams(datastreamsDTO);

        return thingDTO;
    }

}
