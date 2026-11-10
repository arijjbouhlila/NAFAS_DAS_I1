package com.gabesecowatch.locationservice.station;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StationService {

    private final ConcurrentHashMap<Long, Station> stations = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public List<Station> findAll() {
        return stations.values().stream()
                .sorted(Comparator.comparing(Station::id))
                .toList();
    }

    public Station findById(long id) {
        Station station = stations.get(id);
        if (station == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Station not found");
        }
        return station;
    }

    public Station create(StationRequest request) {
        validate(request);
        long id = nextId.getAndIncrement();
        Station station = toStation(id, request);
        stations.put(id, station);
        return station;
    }

    public Station update(long id, StationRequest request) {
        if (!stations.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Station not found");
        }
        validate(request);
        Station station = toStation(id, request);
        stations.put(id, station);
        return station;
    }

    public void delete(long id) {
        if (stations.remove(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Station not found");
        }
    }

    private void validate(StationRequest request) {
        if (request == null || request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        if (request.latitude() == null || request.latitude() < -90 || request.latitude() > 90) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "latitude must be between -90 and 90");
        }
        if (request.longitude() == null || request.longitude() < -180 || request.longitude() > 180) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "longitude must be between -180 and 180");
        }
    }

    private Station toStation(long id, StationRequest request) {
        return new Station(id, request.name().trim(), request.latitude(), request.longitude());
    }
}
