package com.ecosense.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecosense.dto.TimeIOObservationRequestDTO;
import com.ecosense.dto.input.MeasurementIDTO;
import com.ecosense.dto.output.MeasurementODTO;
import com.ecosense.dto.output.MeasurementsODTO;
import com.ecosense.dto.output.PhenomenonODTO;
import com.ecosense.dto.timeio.TimeIOObservationDTO;
import com.ecosense.entity.TimeIODatastream;
import com.ecosense.repository.TimeIODatastreamRepository;
import com.ecosense.service.TimeIOHttpService;
import com.ecosense.service.TimeIOObservationService;

@Service
public class TimeIOObservationServiceImpl implements TimeIOObservationService {

    @Autowired private TimeIOHttpService timeIOHttHtppService;
    @Autowired private TimeIODatastreamRepository timeIODatastreamRepo;

    @Override
    public List<MeasurementsODTO> getObservations(TimeIOObservationRequestDTO request) {
        List<MeasurementsODTO> result = new ArrayList<>();

        List<TimeIODatastream> datastreams = new ArrayList<>();

        for (MeasurementIDTO measurement : request.getMeasurementRequests()) {
            try {
                Optional<TimeIODatastream> datastreamOpt = timeIODatastreamRepo.findById(measurement.getTimeseriesId());
                TimeIODatastream datastream = datastreamOpt.get();
                datastreams.add(datastream);

                Date dateFrom = measurement.getDateFrom();
                Date dateTo = measurement.getDateTo();

                if (dateFrom == null) {
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.WEEK_OF_MONTH, -1); // Default to one week ago
                    dateFrom = cal.getTime();
                }
                if (dateTo == null) {
                    dateTo = new Date(); // Default to current date
                }

                List<TimeIOObservationDTO> observations = timeIOHttHtppService.getAllObservationsForDatastream(datastream, dateFrom, dateTo);
        
                for (TimeIOObservationDTO observation : observations) {
                    MeasurementsODTO measurementsODTO = result.stream()
                        .filter(m -> m.getStation().equals(observation.getDatastreamId().toString()))
                        .findFirst()
                        .orElseGet(() -> {
                            MeasurementsODTO newMeasurementsODTO = new MeasurementsODTO();
                            newMeasurementsODTO.setStation(observation.getDatastreamId() + "");
                            newMeasurementsODTO.setTimeseriesId(observation.getId());

                            PhenomenonODTO phenomDTO = new PhenomenonODTO();
                            phenomDTO.setLabel(request.getPhenomenonId());
                            phenomDTO.setId(observation.getDatastreamId());
                            newMeasurementsODTO.setPhenomenon(phenomDTO);

                            newMeasurementsODTO.setUom(datastream.getUnitMeasSymbol());
                            result.add(newMeasurementsODTO);
                            return newMeasurementsODTO;
                        });

                    List<MeasurementODTO> measurements = measurementsODTO.getMeasurements();
                    if (measurements == null) {
                        measurements = new ArrayList<>();
                        measurementsODTO.setMeasurements(measurements);
                    }
                    
                    MeasurementODTO newMeasurement = new MeasurementODTO();
                    newMeasurement.setDate(observation.getPhenomenonTime());
                    newMeasurement.setValue(observation.getResult().doubleValue());
                    
                    measurements.add(newMeasurement);
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error fetching observations for datastream " + measurement.getTimeseriesId() + " from TimeIO service: " + e.getMessage());
            }
        }

        return result;
    }

}
