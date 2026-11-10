package com.gabesecowatch.locationservice.station;

public record Station(
        Long id,
        String name,
        double latitude,
        double longitude
) {
}
