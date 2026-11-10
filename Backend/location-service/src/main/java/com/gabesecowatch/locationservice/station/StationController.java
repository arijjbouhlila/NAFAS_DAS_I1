package com.gabesecowatch.locationservice.station;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public List<Station> findAll() {
        return stationService.findAll();
    }

    @GetMapping("/{id}")
    public Station findById(@PathVariable long id) {
        return stationService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Station> create(@RequestBody StationRequest request) {
        Station station = stationService.create(request);
        return ResponseEntity.created(URI.create("/api/stations/" + station.id())).body(station);
    }

    @PutMapping("/{id}")
    public Station update(@PathVariable long id, @RequestBody StationRequest request) {
        return stationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        stationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
