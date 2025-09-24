package com.ecosense.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.PointDTO;
import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.timeio.TimeIOLocationDTO;
import com.ecosense.dto.timeio.TimeIOLocationsDTO;
import com.ecosense.dto.timeio.TimeIOThingDTO;
import com.ecosense.entity.TimeIOLocation;
import com.ecosense.entity.TimeIOThing;
import com.ecosense.exception.SimpleException;
import com.ecosense.repository.impl.TimeIOLocationRepository;
import com.ecosense.service.TimeIOLocationService;
import com.ecosense.utils.Utils;

@Service
public class TimeIOLocationServiceImpl implements TimeIOLocationService {

    private @Autowired TimeIOLocationRepository timeIOLocationRepo;

    @Override
    public TimeIOLocationsDTO getAllLocations() throws SimpleException {
        TimeIOLocationsDTO response = new TimeIOLocationsDTO();

        List<TimeIOLocation> locations = timeIOLocationRepo.findAll();

        BoundingBoxDTO boundingBox = new BoundingBoxDTO(Double.MAX_VALUE, Double.MAX_VALUE, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

        List<TimeIOLocationDTO> locationsDTO = new ArrayList<>();
        for (TimeIOLocation location : locations) {
            TimeIOLocationDTO locationDTO = new TimeIOLocationDTO();
            locationDTO.setId(location.getId());
            locationDTO.setIdIot(location.getIdIot());
            locationDTO.setName(location.getName());

            double lng = location.getCoordinates().getCoordinate().x;
			double lat = location.getCoordinates().getCoordinate().y;
            locationDTO.setCoordinates(new PointDTO(lat, lng));

            locationsDTO.add(locationDTO);

			
			double[] minMaxPoints = Utils.transformEpsg(lng, lat, lng, lat,
														"EPSG:4326", "EPSG:3857");
			
            if (minMaxPoints == null) {
				continue;
			}
			BoundingBoxDTO locationBbox = new BoundingBoxDTO(minMaxPoints[0], minMaxPoints[1], minMaxPoints[2], minMaxPoints[3]);
			
			boundingBox = Utils.setBB(locationBbox, boundingBox);
        }

        response.setBoundingBox(boundingBox);
        response.setLocations(locationsDTO);

        return response;
    }

    @Override
    public TimeIOLocationDTO getLocationDetails(Integer id) throws SimpleException {
        Optional<TimeIOLocation> locationOpt = timeIOLocationRepo.findById(id);
        if (!locationOpt.isPresent()) {
            throw new SimpleException(SimpleResponseDTO.DATA_NOT_EXIST);
        }

        TimeIOLocation location = locationOpt.get();

        TimeIOLocationDTO locationDTO = new TimeIOLocationDTO();
        locationDTO.setId(location.getId());
        locationDTO.setIdIot(location.getIdIot());
        locationDTO.setName(location.getName());

        List<TimeIOThing> things = location.getThings();
        List<TimeIOThingDTO> thingsDTO = new ArrayList<>();

        for (TimeIOThing thing : things) {
            TimeIOThingDTO thingDTO = new TimeIOThingDTO();
            thingDTO.setId(thing.getId());
            thingDTO.setIdIot(thing.getIdIot());
            thingDTO.setName(thing.getName());
            thingDTO.setDescription(thing.getDescription());

            thingsDTO.add(thingDTO);
        }

        locationDTO.setThings(thingsDTO);

        return locationDTO;
    }

}
